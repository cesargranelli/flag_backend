package br.com.flagplatform.play.repository;

import br.com.flagplatform.common.persistence.repository.SoftDeleteRepository;
import br.com.flagplatform.play.entity.PlayEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlayRepository extends SoftDeleteRepository<PlayEntity, UUID> {

    List<PlayEntity> findByGameIdOrderByCreatedAtDesc(UUID gameId);

    boolean existsByGameId(UUID gameId);

}
