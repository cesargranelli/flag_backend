package br.com.flagplatform.affiliation.repository;

import br.com.flagplatform.affiliation.entity.InstitutionAffiliationEntity;
import br.com.flagplatform.common.enums.AffiliationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstitutionAffiliationRepository extends JpaRepository<InstitutionAffiliationEntity, UUID> {

    List<InstitutionAffiliationEntity> findAllByInstitutionIdOrderBySeasonDescRequestedAtDesc(UUID institutionId);

    List<InstitutionAffiliationEntity> findAllByOrganizationIdOrderBySeasonDescRequestedAtDesc(UUID organizationId);

    List<InstitutionAffiliationEntity> findAllByOrganizationIdAndSeasonOrderByRequestedAtDesc(UUID organizationId, String season);

    List<InstitutionAffiliationEntity> findAllByOrganizationIdAndStatusOrderBySeasonDescRequestedAtDesc(UUID organizationId, AffiliationStatus status);

    List<InstitutionAffiliationEntity> findAllByOrganizationIdAndSeasonAndStatusOrderByRequestedAtDesc(UUID organizationId, String season, AffiliationStatus status);

    Optional<InstitutionAffiliationEntity> findByInstitutionIdAndOrganizationIdAndSeason(UUID institutionId, UUID organizationId, String season);

    boolean existsByInstitutionIdAndOrganizationIdAndSeason(UUID institutionId, UUID organizationId, String season);
}
