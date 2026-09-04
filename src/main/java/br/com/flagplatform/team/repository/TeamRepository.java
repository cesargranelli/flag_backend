package br.com.flagplatform.team.repository;

import br.com.flagplatform.common.model.Team;
import br.com.flagplatform.common.repository.BaseFirestoreRepository;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository para times.
 * 
 * Acessa a collection 'teams' no Firestore.
 */
@Repository
public class TeamRepository extends BaseFirestoreRepository<Team> {

    public TeamRepository(Firestore firestore) {
        super(firestore);
    }

    @Override
    public String getCollectionName() {
        return "teams";
    }

    @Override
    public Team fromDocument(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        if (data == null) {
            return null;
        }

        return Team.builder()
                .id(doc.getId())
                .organizationId((String) data.get("organizationId"))
                .name((String) data.get("name"))
                .shortName((String) data.get("shortName"))
                .logoUrl((String) data.get("logoUrl"))
                .sport((String) data.get("sport"))
                .divisionId((String) data.get("divisionId"))
                .status((String) data.get("status"))
                .organizationName((String) data.get("organizationName"))
                .organizationLogoUrl((String) data.get("organizationLogoUrl"))
                .build();
    }

    @Override
    public Map<String, Object> toMap(Team item) {
        Map<String, Object> map = new HashMap<>();
        map.put("organizationId", item.getOrganizationId());
        map.put("name", item.getName());
        if (item.getShortName() != null) map.put("shortName", item.getShortName());
        if (item.getLogoUrl() != null) map.put("logoUrl", item.getLogoUrl());
        if (item.getSport() != null) map.put("sport", item.getSport());
        if (item.getDivisionId() != null) map.put("divisionId", item.getDivisionId());
        map.put("status", item.getStatus());
        map.put("createdAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        map.put("updatedAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        if (item.getOrganizationName() != null) map.put("organizationName", item.getOrganizationName());
        if (item.getOrganizationLogoUrl() != null) map.put("organizationLogoUrl", item.getOrganizationLogoUrl());
        return map;
    }
}
