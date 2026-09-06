package br.com.flagplatform.user.service;

import br.com.flagplatform.common.enums.UserRole;
import br.com.flagplatform.common.enums.UserStatus;
import br.com.flagplatform.security.FirebaseUserInfo;
import br.com.flagplatform.security.FirebaseTokenService;
import br.com.flagplatform.security.UserPrincipal;
import br.com.flagplatform.user.TokenProvider;
import br.com.flagplatform.user.UserLookup;
import br.com.flagplatform.user.dto.request.CreateUserRequest;
import br.com.flagplatform.user.dto.request.LoginRequest;
import br.com.flagplatform.user.dto.request.RegisterRequest;
import br.com.flagplatform.user.dto.response.LoginResponse;
import br.com.flagplatform.user.dto.response.UserResponse;
import br.com.flagplatform.user.entity.UserEntity;
import br.com.flagplatform.user.exception.AccountPendingApprovalException;
import br.com.flagplatform.user.exception.EmailAlreadyExistsException;
import br.com.flagplatform.user.exception.InvalidCredentialsException;
import br.com.flagplatform.user.exception.UserNotFoundException;
import br.com.flagplatform.user.mapper.UserMapper;
import br.com.flagplatform.user.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final TokenProvider tokenProvider;

    @Autowired(required = false)
    private FirebaseAuth firebaseAuth;

    @Value("${app.security.default-role:ADMIN_LIGA}")
    private String defaultRole;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = normalize(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        String firebaseUid = null;

        // 1. Cria usuário no Firebase Auth (se configurado)
        if (firebaseAuth == null) {
            log.warn("FirebaseAuth não configurado — usuário NÃO será criado no Firebase Auth. " +
                    "Configure app.firebase.credentials ou variável FIREBASE_CREDENTIALS.");
        }
        if (firebaseAuth != null) {
            try {
                UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                        .setEmail(email)
                        .setPassword(request.password())
                        .setDisplayName(request.name().trim())
                        .setEmailVerified(false);
                UserRecord userRecord = firebaseAuth.createUser(createRequest);
                firebaseUid = userRecord.getUid();
                log.info("Usuário criado no Firebase Auth: uid={}, email={}", firebaseUid, email);
            } catch (FirebaseAuthException ex) {
                log.error("Falha ao criar usuário no Firebase Auth: {}", ex.getMessage());
                log.error("Verifique se o arquivo de service account Firebase é válido (JSON) " +
                        "e se o project-id está correto. ErrorCode: {}", ex.getErrorCode());
                throw new InvalidCredentialsException();
            }
        }

        // 2. Cria registro no PostgreSQL
        UserEntity entity = new UserEntity();
        entity.setName(request.name().trim());
        entity.setEmail(email);
        entity.setFirebaseUid(firebaseUid);
        entity.setPasswordHash(null);
        entity.setRole(UserRole.ORGANIZER);
        entity.setStatus(UserStatus.PENDING);

        return mapper.toResponse(userRepository.save(entity));
    }

    public LoginResponse login(LoginRequest request) {
        String token = request.firebaseIdToken();

        // 1. Valida Firebase ID Token
        FirebaseUserInfo firebaseInfo = firebaseTokenService.verifyToken(token)
                .orElseThrow(InvalidCredentialsException::new);

        // 2. Procura ou provisiona usuário
        UserEntity user = getOrProvisionFirebaseUser(firebaseInfo);

        // 3. Verifica status ativo
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountPendingApprovalException(
                    "Account is not active (status: %s).".formatted(user.getStatus()));
        }

        // 4. Gera token JWT para sessão backend (validade curta)
        String jwt = tokenProvider.generateToken(user.getEmail());

        return new LoginResponse(
                jwt,
                "Bearer",
                tokenProvider.getExpirationSeconds(),
                mapper.toResponse(user));
    }

    @Transactional
    public UserEntity getOrProvisionFirebaseUser(FirebaseUserInfo firebaseInfo) {
        String uid = firebaseInfo.uid();
        String email = normalize(firebaseInfo.email());

        // 1. Busca pelo firebase_uid
        Optional<UserEntity> userByUid = userRepository.findByFirebaseUid(uid);
        if (userByUid.isPresent()) {
            return userByUid.get();
        }

        // 2. Fallback: busca por email para vincular usuário pré-existente
        if (email != null && !email.isBlank()) {
            Optional<UserEntity> userByEmail = userRepository.findByEmailIgnoreCase(email);
            if (userByEmail.isPresent()) {
                UserEntity existing = userByEmail.get();
                if (existing.getFirebaseUid() == null) {
                    existing.setFirebaseUid(uid);
                    return userRepository.save(existing);
                }
                return existing;
            }
        }

        // 3. Auto-provisionamento inicial
        UserEntity newUser = new UserEntity();
        String name = firebaseInfo.name();
        if (name == null || name.isBlank()) {
            name = (email != null && !email.isBlank()) ? email.split("@")[0] : "Usuário";
        }
        newUser.setName(name.trim());
        newUser.setEmail(email != null && !email.isBlank() ? email : uid + "@firebase.user");
        newUser.setFirebaseUid(uid);
        newUser.setPasswordHash(null);
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
        entity.setPasswordHash(null);
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
