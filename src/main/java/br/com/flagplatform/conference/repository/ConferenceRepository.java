package br.com.flagplatform.conference.repository;

import br.com.flagplatform.common.persistence.repository.SoftDeleteRepository;
import br.com.flagplatform.conference.entity.ConferenceEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConferenceRepository extends SoftDeleteRepository<ConferenceEntity, UUID> {

    List<ConferenceEntity> findAllByCompetitionIdOrderByNameAsc(UUID competitionId);

    boolean existsByCompetitionIdAndNameIgnoreCase(UUID competitionId, String name);

    boolean existsByCompetitionIdAndNameIgnoreCaseAndIdNot(UUID competitionId, String name, UUID id);

}
