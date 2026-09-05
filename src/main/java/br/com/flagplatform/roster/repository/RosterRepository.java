package br.com.flagplatform.roster.repository;

import br.com.flagplatform.common.persistence.repository.SoftDeleteRepository;
import br.com.flagplatform.roster.entity.RosterEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RosterRepository extends SoftDeleteRepository<RosterEntity, UUID> {

    List<RosterEntity> findAllByTeamIdAndCompetitionIdOrderByCreatedAtAsc(UUID teamId, UUID competitionId);

    Optional<RosterEntity> findByTeamIdAndCompetitionId(UUID teamId, UUID competitionId);

    List<RosterEntity> findAllByTeamIdOrderByCreatedAtDesc(UUID teamId);

}
