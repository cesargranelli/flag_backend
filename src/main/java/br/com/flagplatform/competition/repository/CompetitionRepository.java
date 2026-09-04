package br.com.flagplatform.competition.repository;

import br.com.flagplatform.common.model.Competition;
import br.com.flagplatform.common.repository.BaseFirestoreRepository;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository para competições.
 * 
 * Acessa a collection 'competitions' no Firestore.
 */
@Repository
public class CompetitionRepository extends BaseFirestoreRepository<Competition> {

    public CompetitionRepository(Firestore firestore) {
        super(firestore);
    }

    @Override
    public String getCollectionName() {
        return "competitions";
    }

    @Override
    public Competition fromDocument(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        if (data == null) {
            return null;
        }

        return Competition.builder()
                .id(doc.getId())
                .seasonId((String) data.get("seasonId"))
                .organizationId((String) data.get("organizationId"))
                .name((String) data.get("name"))
                .sport((String) data.get("sport"))
                .modality((String) data.get("modality"))
                .gender((String) data.get("gender"))
                .ageGroup((String) data.get("ageGroup"))
                .groupingType((String) data.get("groupingType"))
                .venueId((String) data.get("venueId"))
                .startDate(data.get("startDate") != null 
                    ? LocalDate.parse((String) data.get("startDate")) 
                    : null)
                .endDate(data.get("endDate") != null 
                    ? LocalDate.parse((String) data.get("endDate")) 
                    : null)
                .status((String) data.get("status"))
                .eligibilityRules((Map<String, Object>) data.get("eligibilityRules"))
                .organizationName((String) data.get("organizationName"))
                .seasonName((String) data.get("seasonName"))
                .venueName((String) data.get("venueName"))
                .build();
    }

    @Override
    public Map<String, Object> toMap(Competition item) {
        Map<String, Object> map = new HashMap<>();
        if (item.getSeasonId() != null) map.put("seasonId", item.getSeasonId());
        map.put("organizationId", item.getOrganizationId());
        map.put("name", item.getName());
        map.put("sport", item.getSport());
        if (item.getModality() != null) map.put("modality", item.getModality());
        if (item.getGender() != null) map.put("gender", item.getGender());
        if (item.getAgeGroup() != null) map.put("ageGroup", item.getAgeGroup());
        if (item.getGroupingType() != null) map.put("groupingType", item.getGroupingType());
        if (item.getVenueId() != null) map.put("venueId", item.getVenueId());
        if (item.getStartDate() != null) map.put("startDate", item.getStartDate().toString());
        if (item.getEndDate() != null) map.put("endDate", item.getEndDate().toString());
        map.put("status", item.getStatus());
        if (item.getEligibilityRules() != null) map.put("eligibilityRules", item.getEligibilityRules());
        map.put("createdAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        map.put("updatedAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        if (item.getOrganizationName() != null) map.put("organizationName", item.getOrganizationName());
        if (item.getSeasonName() != null) map.put("seasonName", item.getSeasonName());
        if (item.getVenueName() != null) map.put("venueName", item.getVenueName());
        return map;
    }
}
