package br.com.flagplatform.season.repository;

import br.com.flagplatform.common.model.Season;
import br.com.flagplatform.common.repository.BaseFirestoreRepository;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository para temporadas.
 * 
 * Acessa a collection 'seasons' no Firestore.
 */
@Repository
public class SeasonRepository extends BaseFirestoreRepository<Season> {

    public SeasonRepository(Firestore firestore) {
        super(firestore);
    }

    @Override
    public String getCollectionName() {
        return "seasons";
    }

    @Override
    public Season fromDocument(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        if (data == null) {
            return null;
        }

        return Season.builder()
                .id(doc.getId())
                .organizationId((String) data.get("organizationId"))
                .name((String) data.get("name"))
                .sport((String) data.get("sport"))
                .startDate(data.get("startDate") != null 
                    ? LocalDate.parse((String) data.get("startDate")) 
                    : null)
                .endDate(data.get("endDate") != null 
                    ? LocalDate.parse((String) data.get("endDate")) 
                    : null)
                .status((String) data.get("status"))
                .build();
    }

    @Override
    public Map<String, Object> toMap(Season item) {
        Map<String, Object> map = new HashMap<>();
        map.put("organizationId", item.getOrganizationId());
        map.put("name", item.getName());
        map.put("sport", item.getSport());
        if (item.getStartDate() != null) map.put("startDate", item.getStartDate().toString());
        if (item.getEndDate() != null) map.put("endDate", item.getEndDate().toString());
        map.put("status", item.getStatus());
        map.put("createdAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        map.put("updatedAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        return map;
    }
}
