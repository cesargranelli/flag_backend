package br.com.flagplatform.user.service;

import br.com.flagplatform.common.enums.UserRole;
import br.com.flagplatform.security.FirebaseTokenProvider;
import br.com.flagplatform.user.dto.request.SetCustomClaimsRequest;
import br.com.flagplatform.user.dto.response.FirebaseLinkResponse;
import br.com.flagplatform.user.dto.response.FirebaseStatusResponse;
import br.com.flagplatform.user.entity.UserEntity;
import br.com.flagplatform.user.exception.FirebaseOperationException;
import br.com.flagplatform.user.exception.FirebaseUidAlreadyLinkedException;
import br.com.flagplatform.user.exception.FirebaseUidConflictException;
import br.com.flagplatform.user.exception.FirebaseUserNotLinkedException;
import br.com.flagplatform.user.exception.UserNotFoundException;
import br.com.flagplatform.user.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Endpoints administrativos para gerenciar a vinculação entre usuários
 * PostgreSQL e contas Firebase Auth, além da sincronização de Custom Claims.
 *
 * <p>Fase 2 da migração JWT custom → Firebase Auth (issue #31).
 * Refs:
 * <ul>
 *   <li>ADR-004 — Firebase Auth + Custom Claims</li>
 *   <li>Issue #19 — Fase 1 (configuração Firebase + filtro)</li>
 * </ul>
 *
 * <p>Custom Claims geradas seguem o padrão definido no plano de migração:
 * <pre>{@code
 * {
 *   "role": "ORG_ADMIN",
 *   "organization_id": "uuid",
 *   "club_id": "uuid",
 *   "skills": ["athlete", "coach"]
 * }
 * }</pre>
 *
 * <p><b>Mapeamento de roles:</b> o role PostgreSQL ({@link UserRole#ADMIN},
 * {@link UserRole#ORGANIZER}, {@link UserRole#MESA}) é convertido para o role
 * Firebase seguindo a hierarquia do ADR-004:
 * <ul>
 *   <li>{@code ADMIN} → {@code SUPER_ADMIN}</li>
 *   <li>{@code ORGANIZER} → {@code ORG_ADMIN}</li>
 *   <li>{@code MESA} → {@code USER} (perfil operacional)</li>
 * </ul>
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.auth.firebase-enabled", havingValue = "true")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FirebaseAdminService {

    private final UserRepository userRepository;
    private final FirebaseTokenProvider firebaseTokenProvider;
    private final FirebaseAuth firebaseAuth;

    /**
     * Vincula um usuário PostgreSQL a uma conta Firebase Auth. Se o usuário ainda
     * não possui conta Firebase, cria uma nova (com email + senha temporária).
     *
     * @param userId ID do usuário no PostgreSQL
     * @return resposta com o UID do Firebase
     */
    @Transactional
    public FirebaseLinkResponse linkUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getFirebaseUid() != null && !user.getFirebaseUid().isBlank()) {
            log.info("Usuário '{}' já vinculado ao Firebase UID '{}', reaproveitando",
                    user.getEmail(), user.getFirebaseUid());
            return new FirebaseLinkResponse(user.getFirebaseUid(), user.getId().toString(), user.getEmail());
        }

        String firebaseUid;
        try {
            firebaseUid = resolveOrCreateFirebaseUid(user);
        } catch (FirebaseAuthException e) {
            log.error("Falha ao criar/vincular usuário Firebase para '{}': {}",
                    user.getEmail(), e.getMessage());
            throw new FirebaseOperationException("link user to Firebase", e.getMessage());
        }

        // Valida que o UID não está vinculado a outro usuário PostgreSQL
        userRepository.findByFirebaseUid(firebaseUid).ifPresent(existing -> {
            if (!existing.getId().equals(user.getId())) {
                throw new FirebaseUidAlreadyLinkedException(firebaseUid, existing.getEmail());
            }
        });

        user.setFirebaseUid(firebaseUid);
        userRepository.save(user);

        log.info("Usuário '{}' vinculado ao Firebase UID '{}'", user.getEmail(), firebaseUid);
        return new FirebaseLinkResponse(firebaseUid, user.getId().toString(), user.getEmail());
    }

    /**
     * Define as Custom Claims de um usuário no Firebase e sincroniza com o PostgreSQL.
     *
     * @param userId  ID do usuário
     * @param request request com role, organizationId, clubId, skills
     * @return status atualizado do usuário no Firebase
     */
    @Transactional
    public FirebaseStatusResponse setCustomClaims(UUID userId, SetCustomClaimsRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getFirebaseUid() == null || user.getFirebaseUid().isBlank()) {
            throw new FirebaseUserNotLinkedException(userId);
        }

        Map<String, Object> claims = buildClaims(request);
        try {
            firebaseTokenProvider.setCustomClaims(user.getFirebaseUid(), claims);
        } catch (FirebaseAuthException e) {
            log.error("Falha ao definir custom claims para UID '{}': {}",
                    user.getFirebaseUid(), e.getMessage());
            throw new FirebaseOperationException("set custom claims", e.getMessage());
        }

        // Sincroniza no PostgreSQL (source of truth)
        if (request.organizationId() != null) {
            user.setOrganizationId(request.organizationId());
        }
        if (request.clubId() != null) {
            user.setClubId(request.clubId());
        }
        if (request.skills() != null) {
            user.setSkills(request.skills());
        }
        userRepository.save(user);

        log.info("Custom claims definidas para '{}' (role={}, orgId={}, clubId={})",
                user.getEmail(), request.role(),
                request.organizationId(), request.clubId());

        return getStatus(userId);
    }

    /**
     * Recupera o status de vinculação Firebase de um usuário.
     *
     * @param userId ID do usuário
     * @return status com linkage e claims
     */
    public FirebaseStatusResponse getStatus(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String firebaseUid = user.getFirebaseUid();
        boolean linked = firebaseUid != null && !firebaseUid.isBlank();

        if (!linked) {
            return new FirebaseStatusResponse(
                    user.getId(),
                    user.getEmail(),
                    false,
                    null,
                    null,
                    false
            );
        }

        FirebaseStatusResponse.FirebaseClaimsDto claimsDto = null;
        Map<String, Object> claims = null;
        try {
            UserRecord record = firebaseAuth.getUser(firebaseUid);
            claims = record.getCustomClaims();
        } catch (FirebaseAuthException e) {
            log.warn("Falha ao obter claims do Firebase para UID '{}': {}",
                    firebaseUid, e.getMessage());
            throw new FirebaseOperationException("fetch custom claims", e.getMessage());
        }

        if (claims != null) {
            claimsDto = new FirebaseStatusResponse.FirebaseClaimsDto(
                    Objects.toString(claims.get("role"), null),
                    Objects.toString(claims.get("organization_id"), null),
                    Objects.toString(claims.get("club_id"), null),
                    extractSkills(claims.get("skills"))
            );
        }

        boolean synced = areClaimsSyncedWithDb(claims, user);

        return new FirebaseStatusResponse(
                user.getId(),
                user.getEmail(),
                true,
                firebaseUid,
                claimsDto,
                synced
        );
    }

    private boolean areClaimsSyncedWithDb(Map<String, Object> claims, UserEntity user) {
        if (claims == null) {
            return user.getOrganizationId() == null && user.getClubId() == null;
        }
        String claimsOrg = Objects.toString(claims.get("organization_id"), null);
        String claimsClub = Objects.toString(claims.get("club_id"), null);
        List<String> claimsSkills = extractSkills(claims.get("skills"));

        boolean orgOk = Objects.equals(claimsOrg,
                user.getOrganizationId() == null ? null : user.getOrganizationId().toString());
        boolean clubOk = Objects.equals(claimsClub,
                user.getClubId() == null ? null : user.getClubId().toString());
        boolean skillsOk = Objects.equals(claimsSkills,
                user.getSkills() == null ? List.of() : user.getSkills());

        return orgOk && clubOk && skillsOk;
    }

    private List<String> extractSkills(Object skillsClaim) {
        if (skillsClaim instanceof List<?> list) {
            return list.stream().filter(Objects::nonNull).map(Object::toString).toList();
        }
        return List.of();
    }

    /**
     * Resolve o UID Firebase de um usuário: tenta buscar pelo e-mail; se não existir, cria.
     * Se o usuário já tem UID preenchido no banco, valida conflito e retorna.
     */
    private String resolveOrCreateFirebaseUid(UserEntity user) throws FirebaseAuthException {
        return resolveOrCreateFirebaseUid(user.getEmail(), user.getName(), user.getPasswordHash());
    }

    /**
     * Resolve o UID Firebase de um usuário: tenta buscar pelo e-mail; se não existir, cria.
     * Versão com parâmetros individuais para uso pelo AuthService.
     *
     * @param email         e-mail do usuário
     * @param displayName   nome de exibição
     * @param passwordHash  hash da senha (ignorado na criação via Admin SDK)
     */
    public String resolveOrCreateFirebaseUid(String email, String displayName, String passwordHash) throws FirebaseAuthException {
        // Tenta resolver UID existente no Firebase pelo email
        try {
            UserRecord existing = firebaseAuth.getUserByEmail(email);
            log.info("Encontrada conta Firebase existente para '{}': UID='{}'",
                    email, existing.getUid());
            return existing.getUid();
        } catch (FirebaseAuthException notFound) {
            // Não existe — cria nova conta
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setEmailVerified(true)
                    .setDisplayName(displayName);

            UserRecord created = firebaseAuth.createUser(createRequest);
            log.info("Conta Firebase criada para '{}': UID='{}'", email, created.getUid());
            return created.getUid();
        }
    }

    /**
     * Constrói o mapa de Custom Claims a partir do request, removendo chaves null.
     */
    private Map<String, Object> buildClaims(SetCustomClaimsRequest request) {
        Map<String, Object> claims = new HashMap<>();
        if (request.role() != null && !request.role().isBlank()) {
            claims.put("role", request.role().toUpperCase());
        }
        if (request.organizationId() != null) {
            claims.put("organization_id", request.organizationId().toString());
        }
        if (request.clubId() != null) {
            claims.put("club_id", request.clubId().toString());
        }
        if (request.skills() != null) {
            claims.put("skills", request.skills());
        }
        return claims;
    }

    /**
     * Mapeia o role PostgreSQL para o equivalente Firebase (ADR-004).
     */
    public static String mapRoleToFirebase(UserRole role) {
        if (role == null) {
            return "USER";
        }
        return switch (role) {
            case ADMIN -> "SUPER_ADMIN";
            case ORGANIZER -> "ORG_ADMIN";
            case MESA -> "USER";
        };
    }

    /**
     * Helper para validar que não há conflito de UID (uso interno).
     */
    void assertNoConflict(UserEntity user, String firebaseUid) {
        userRepository.findByFirebaseUid(firebaseUid).ifPresent(existing -> {
            if (!existing.getId().equals(user.getId())) {
                throw new FirebaseUidConflictException(existing.getFirebaseUid(), firebaseUid);
            }
        });
    }
}