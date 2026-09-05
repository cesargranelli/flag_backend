package br.com.flagplatform.round.repository;

import br.com.flagplatform.common.persistence.repository.SoftDeleteRepository;
import br.com.flagplatform.round.entity.RoundEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoundRepository extends SoftDeleteRepository<RoundEntity, UUID> {

    List<RoundEntity> findAllByCompetitionIdOrderByNumberAsc(UUID competitionId);

    List<RoundEntity> findAllByCompetitionId(UUID competitionId);

    boolean existsByCompetitionIdAndNumber(UUID competitionId, Integer number);

    boolean existsByCompetitionIdAndNumberAndIdNot(UUID competitionId, Integer number, UUID id);

}
