package br.com.flagplatform.venue.repository;

import br.com.flagplatform.common.model.Venue;
import br.com.flagplatform.common.repository.BaseFirestoreRepository;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository para venues (campos de jogo).
 * 
 * Acessa a collection 'venues' no Firestore.
 */
@Repository
public class VenueRepository extends BaseFirestoreRepository<Venue> {

    public VenueRepository(Firestore firestore) {
        super(firestore);
    }

    @Override
    public String getCollectionName() {
        return "venues";
    }

    @Override
    public Venue fromDocument(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        if (data == null) {
            return null;
        }

        return Venue.builder()
                .id(doc.getId())
                .name((String) data.get("name"))
                .logoUrl((String) data.get("logoUrl"))
                .address((Map<String, Object>) data.get("address"))
                .mapsUrl((String) data.get("mapsUrl"))
                .build();
    }

    @Override
    public Map<String, Object> toMap(Venue item) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", item.getName());
        if (item.getLogoUrl() != null) map.put("logoUrl", item.getLogoUrl());
        if (item.getAddress() != null) map.put("address", item.getAddress());
        if (item.getMapsUrl() != null) map.put("mapsUrl", item.getMapsUrl());
        map.put("createdAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        map.put("updatedAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        return map;
    }
}
