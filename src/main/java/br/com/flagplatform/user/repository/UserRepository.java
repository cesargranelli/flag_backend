package br.com.flagplatform.user.repository;

import br.com.flagplatform.common.enums.UserStatus;
import br.com.flagplatform.common.persistence.repository.SoftDeleteRepository;
import br.com.flagplatform.user.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends SoftDeleteRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<UserEntity> findAllByOrderByNameAsc();

    List<UserEntity> findAllByStatusOrderByCreatedAtAsc(UserStatus status);

}
