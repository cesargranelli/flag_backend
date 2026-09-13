package br.com.flagplatform.affiliation.repository;

import br.com.flagplatform.affiliation.entity.AffiliationWindowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AffiliationWindowRepository extends JpaRepository<AffiliationWindowEntity, UUID> {

    List<AffiliationWindowEntity> findAllByOrganizationIdOrderBySeasonDesc(UUID organizationId);

    Optional<AffiliationWindowEntity> findByOrganizationIdAndSeason(UUID organizationId, String season);

    List<AffiliationWindowEntity> findAllByStatus(String status);
}
