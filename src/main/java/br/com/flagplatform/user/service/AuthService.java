package br.com.flagplatform.user.service;

import br.com.flagplatform.common.enums.UserRole;
import br.com.flagplatform.common.enums.UserStatus;
import br.com.flagplatform.security.FirebaseUserInfo;
import br.com.flagplatform.security.FirebaseTokenService;
import br.com.flagplatform.security.UserPrincipal;
import br.com.flagplatform.user.UserLookup;
import br.com.flagplatform.user.dto.request.CreateUserRequest;
import br.com.flagplatform.user.dto.request.RegisterRequest;
import br.com.flagplatform.user.dto.response.UserResponse;
import br.com.flagplatform.user.entity.UserEntity;
import br.com.flagplatform.user.exception.AccountPendingApprovalException;
import br.com.flagplatform.user.exception.EmailAlreadyExistsException;
import br.com.flagplatform.user.exception.InvalidCredentialsException;
import br.com.flagplatform.user.exception.UserNotFoundException;
import br.com.flagplatform.user.mapper.UserMapper;
import br.com.flagplatform.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService implements UserLookup {

    private final UserRepository userRepository;
    private final FirebaseTokenService firebaseTokenService;
    private final UserMapper mapper;

    @Value("${app.security.default-role:ADMIN_LIGA}")
    private String defaultRole;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = normalize(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        // A criação no Firebase Auth é responsabilidade do frontend (via Firebase SDK).
        // O backend apenas cria o registro no PostgreSQL.

        // Cria registro no PostgreSQL
        //    - Primeiro usuário: ACTIVE + ADMIN_LIGA (permite login imediato)
        //    - Demais: PENDING (aguardando aprovação)
        UserEntity entity = new UserEntity();
        entity.setName(request.name().trim());
        entity.setEmail(email);
        entity.setFirebaseUid(null);
        entity.setRole(UserRole.ORGANIZER);

        boolean isFirstUser = userRepository.count() == 0;
        if (isFirstUser) {
            entity.setStatus(UserStatus.ACTIVE);
            entity.setRole(UserRole.ADMIN_LIGA);
            log.info("Primeiro usuário registrado — auto-ativando como ADMIN_LIGA: email={}", email);
        } else {
            entity.setStatus(UserStatus.PENDING);
        }

        UserResponse response = mapper.toResponse(userRepository.save(entity));
        log.info("Usuário registrado no PostgreSQL ({}): email={}, id={}",
                entity.getStatus(), email, entity.getId());
        return response;
    }

    /**
     * Procura ou provisiona usuário a partir de um Firebase ID Token validado.
     * Chamado pelo {@link br.com.flagplatform.security.JwtAuthenticationFilter}.
     */
    @Transactional
    public UserEntity getOrProvisionFirebaseUser(FirebaseUserInfo firebaseInfo) {
        String uid = firebaseInfo.uid();
        String email = normalize(firebaseInfo.email());

        // 1. Busca pelo firebase_uid
        Optional<UserEntity> userByUid = userRepository.findByFirebaseUid(uid);
        if (userByUid.isPresent()) {
            return userByUid.get();
        }

        // 2. Fallback: busca por email para vincular usuário pré-existente (criado via /register)
        if (email != null && !email.isBlank()) {
            Optional<UserEntity> userByEmail = userRepository.findByEmailIgnoreCase(email);
            if (userByEmail.isPresent()) {
                UserEntity existing = userByEmail.get();
                if (existing.getFirebaseUid() == null) {
                    existing.setFirebaseUid(uid);
                    log.info("Vinculando firebase_uid ao usuário existente: email={}, uid={}", email, uid);
                    return userRepository.save(existing);
                }
                return existing;
            }
        }

        // 3. Auto-provisionamento (usuário criado diretamente no Firebase Auth, sem /register)
        UserEntity newUser = new UserEntity();
        String name = firebaseInfo.name();
        if (name == null || name.isBlank()) {
            name = (email != null && !email.isBlank()) ? email.split("@")[0] : "Usuário";
        }
        newUser.setName(name.trim());
        newUser.setEmail(email != null && !email.isBlank() ? email : uid + "@firebase.user");
        newUser.setFirebaseUid(uid);
        newUser.setStatus(UserStatus.ACTIVE);

        // Role: ADMIN_LIGA se primeiro usuário ou default configurado
        boolean isFirstUser = userRepository.count() == 0;
        UserRole assignedRole;
        if (isFirstUser) {
            assignedRole = UserRole.ADMIN_LIGA;
        } else {
            try {
                assignedRole = UserRole.valueOf(defaultRole);
            } catch (Exception e) {
                assignedRole = UserRole.ADMIN_LIGA;
            }
        }
        newUser.setRole(assignedRole);

        // Extrai claims opcionais (organization_id, club_id)
        if (firebaseInfo.claims() != null) {
            Object orgClaim = firebaseInfo.claims().get("organization_id");
            if (orgClaim != null) {
                try {
                    newUser.setOrganizationId(UUID.fromString(orgClaim.toString()));
                } catch (IllegalArgumentException ignored) {
                }
            }
            Object clubClaim = firebaseInfo.claims().get("club_id");
            if (clubClaim != null) {
                try {
                    newUser.setClubId(UUID.fromString(clubClaim.toString()));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        return userRepository.save(newUser);
    }

    public UserResponse me(Object principal) {
        if (principal instanceof UserPrincipal up) {
            if (up.getFirebaseUid() != null) {
                UserEntity user = userRepository.findByFirebaseUid(up.getFirebaseUid())
                        .or(() -> userRepository.findByEmailIgnoreCase(normalize(up.getEmail())))
                        .orElseThrow(InvalidCredentialsException::new);
                return mapper.toResponse(user);
            }
            if (up.getEmail() != null) {
                return me(up.getEmail());
            }
        }
        if (principal instanceof String emailOrUid) {
            return me(emailOrUid);
        }
        throw new InvalidCredentialsException();
    }

    public UserResponse me(String emailOrUid) {
        String normalized = normalize(emailOrUid);
        UserEntity user = userRepository.findByFirebaseUid(emailOrUid)
                .or(() -> userRepository.findByEmailIgnoreCase(normalized))
                .orElseThrow(InvalidCredentialsException::new);
        return mapper.toResponse(user);
    }

    @Override
    public UUID findUserIdByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(normalize(email))
                .orElseThrow(InvalidCredentialsException::new)
                .getId();
    }

    @Override
    public boolean isAdminByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(normalize(email))
                .map(user -> user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.ADMIN_LIGA)
                .orElse(false);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String email = normalize(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        UserEntity entity = new UserEntity();
        entity.setName(request.name().trim());
        entity.setEmail(email);
        entity.setRole(request.role());
        entity.setStatus(UserStatus.ACTIVE);

        return mapper.toResponse(userRepository.save(entity));
    }

    public List<UserResponse> findAll() {
        return mapper.toResponseList(userRepository.findAllByOrderByNameAsc());
    }

    public List<UserResponse> listPending() {
        return mapper.toResponseList(
                userRepository.findAllByStatusOrderByCreatedAtAsc(UserStatus.PENDING));
    }

    @Transactional
    public UserResponse approve(UUID id) {
        UserEntity user = findEntityById(id);
        user.setStatus(UserStatus.ACTIVE);
        return mapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse reject(UUID id) {
        UserEntity user = findEntityById(id);
        user.setStatus(UserStatus.REJECTED);
        return mapper.toResponse(userRepository.save(user));
    }

    private UserEntity findEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

}
