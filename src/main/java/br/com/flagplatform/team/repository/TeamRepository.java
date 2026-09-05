package br.com.flagplatform.team.repository;

import br.com.flagplatform.common.persistence.repository.SoftDeleteRepository;
import br.com.flagplatform.team.entity.TeamEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository extends SoftDeleteRepository<TeamEntity, UUID> {

    List<TeamEntity> findAllByOrganizationIdOrderByNameAsc(UUID organizationId);

    boolean existsByOrganizationIdAndNameIgnoreCase(UUID organizationId, String name);

    boolean existsByOrganizationIdAndNameIgnoreCaseAndIdNot(UUID organizationId, String name, UUID id);

}
