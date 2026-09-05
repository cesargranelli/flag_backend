package br.com.flagplatform.roster.repository;

import br.com.flagplatform.common.persistence.repository.SoftDeleteRepository;
import br.com.flagplatform.roster.entity.RosterEntryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RosterEntryRepository extends SoftDeleteRepository<RosterEntryEntity, UUID> {

    List<RosterEntryEntity> findAllByRosterIdOrderByCreatedAtAsc(UUID rosterId);

    boolean existsByRosterIdAndAthleteId(UUID rosterId, UUID athleteId);

    Optional<RosterEntryEntity> findByRosterIdAndAthleteId(UUID rosterId, UUID athleteId);

}
