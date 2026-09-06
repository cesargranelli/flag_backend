package br.com.flagplatform.user.config;

import br.com.flagplatform.common.enums.UserRole;
import br.com.flagplatform.common.enums.UserStatus;
import br.com.flagplatform.user.entity.UserEntity;
import br.com.flagplatform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Seed de usuários para o ambiente de staging (testes E2E).
 * <p>
 * Cria usuários com status ACTIVE para permitir login direto no Admin Web,
 * já que o registro público gera contas PENDING.
 * <p>
 * A autenticação é feita via Firebase Auth SDK no frontend —
 * não é necessário armazenar hash de senha.
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Profile("staging")
public class StagingDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        List<UserEntity> applied = new ArrayList<>();
        seed("organizer@flag.test", "Organizador Staging", UserRole.ORGANIZER, applied);
        seed("admin@flag.test", "Admin Staging", UserRole.ADMIN_LIGA, applied);

        if (!applied.isEmpty()) {
            log.info("Usuários de staging aplicados ({} itens): {}", applied.size(),
                    applied.stream().map(UserEntity::getEmail).toList());
        }
    }

    private void seed(String email, String name, UserRole role, List<UserEntity> applied) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        UserEntity entity = new UserEntity();
        entity.setName(name);
        entity.setEmail(email);
        entity.setRole(role);
        entity.setStatus(UserStatus.ACTIVE);
        userRepository.save(entity);
        applied.add(entity);
    }

}
