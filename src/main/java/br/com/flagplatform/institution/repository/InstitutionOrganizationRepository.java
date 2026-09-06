package br.com.flagplatform.institution.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InstitutionOrganizationRepository {

    private final JdbcTemplate jdbc;

    public List<UUID> findOrganizationIds(UUID institutionId) {
        return jdbc.queryForList(
                "SELECT organization_id FROM platform.institution_organizations WHERE institution_id = ?",
                UUID.class, institutionId);
    }

    public void setOrganizations(UUID institutionId, List<UUID> organizationIds) {
        jdbc.update("DELETE FROM platform.institution_organizations WHERE institution_id = ?", institutionId);
        if (organizationIds == null || organizationIds.isEmpty()) return;
        for (UUID orgId : organizationIds) {
            jdbc.update("INSERT INTO platform.institution_organizations(institution_id, organization_id) VALUES (?,?)",
                    institutionId, orgId);
        }
    }
}
