package br.com.flagplatform.organization.repository;

import br.com.flagplatform.common.model.Organization;
import br.com.flagplatform.common.repository.BaseFirestoreRepository;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Repository para organizações.
 * 
 * Acessa a collection 'organizations' no Firestore.
 */
@Repository
public class OrganizationRepository extends BaseFirestoreRepository<Organization> {

    public OrganizationRepository(Firestore firestore) {
        super(firestore);
    }

    @Override
    public String getCollectionName() {
        return "organizations";
    }

    @Override
    public Organization fromDocument(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        if (data == null) {
            return null;
        }

        return Organization.builder()
                .id(doc.getId())
                .name((String) data.get("name"))
                .tradeName((String) data.get("tradeName"))
                .parentId((String) data.get("parentId"))
                .type((String) data.get("type"))
                .document((String) data.get("document"))
                .logoUrl((String) data.get("logoUrl"))
                .primaryColor((String) data.get("primaryColor"))
                .secondaryColor((String) data.get("secondaryColor"))
                .tertiaryColor((String) data.get("tertiaryColor"))
                .quaternaryColor((String) data.get("quaternaryColor"))
                .email((String) data.get("email"))
                .phone((String) data.get("phone"))
                .website((String) data.get("website"))
                .instagram((String) data.get("instagram"))
                .country((String) data.get("country"))
                .state((String) data.get("state"))
                .city((String) data.get("city"))
                .timezone((String) data.get("timezone"))
                .locale((String) data.get("locale"))
                .status((String) data.get("status"))
                .build();
    }

    @Override
    public Map<String, Object> toMap(Organization item) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", item.getName());
        if (item.getTradeName() != null) map.put("tradeName", item.getTradeName());
        if (item.getParentId() != null) map.put("parentId", item.getParentId());
        if (item.getType() != null) map.put("type", item.getType());
        if (item.getDocument() != null) map.put("document", item.getDocument());
        if (item.getLogoUrl() != null) map.put("logoUrl", item.getLogoUrl());
        if (item.getPrimaryColor() != null) map.put("primaryColor", item.getPrimaryColor());
        if (item.getSecondaryColor() != null) map.put("secondaryColor", item.getSecondaryColor());
        if (item.getTertiaryColor() != null) map.put("tertiaryColor", item.getTertiaryColor());
        if (item.getQuaternaryColor() != null) map.put("quaternaryColor", item.getQuaternaryColor());
        if (item.getEmail() != null) map.put("email", item.getEmail());
        if (item.getPhone() != null) map.put("phone", item.getPhone());
        if (item.getWebsite() != null) map.put("website", item.getWebsite());
        if (item.getInstagram() != null) map.put("instagram", item.getInstagram());
        if (item.getCountry() != null) map.put("country", item.getCountry());
        if (item.getState() != null) map.put("state", item.getState());
        if (item.getCity() != null) map.put("city", item.getCity());
        if (item.getTimezone() != null) map.put("timezone", item.getTimezone());
        if (item.getLocale() != null) map.put("locale", item.getLocale());
        map.put("status", item.getStatus());
        map.put("createdAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        map.put("updatedAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        return map;
    }
}
