package br.com.flagplatform.person.repository;

import br.com.flagplatform.common.model.Person;
import br.com.flagplatform.common.repository.BaseFirestoreRepository;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Repository para pessoas (atletas, técnicos, árbitros).
 * 
 * Acessa a collection 'persons' no Firestore.
 */
@Repository
public class PersonRepository extends BaseFirestoreRepository<Person> {

    public PersonRepository(Firestore firestore) {
        super(firestore);
    }

    @Override
    public String getCollectionName() {
        return "persons";
    }

    @Override
    public Person fromDocument(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        if (data == null) {
            return null;
        }

        return Person.builder()
                .id(doc.getId())
                .name((String) data.get("name"))
                .email((String) data.get("email"))
                .phone((String) data.get("phone"))
                .photoUrl((String) data.get("photoUrl"))
                .gender((String) data.get("gender"))
                .birthDate(data.get("birthDate") != null 
                    ? LocalDate.parse((String) data.get("birthDate")) 
                    : null)
                .computedAgeGroup((String) data.get("computedAgeGroup"))
                .roles((List<String>) data.get("roles"))
                .status((String) data.get("status"))
                .createdAt(data.get("createdAt") != null 
                    ? ((com.google.cloud.Timestamp) data.get("createdAt")).toDate().toInstant() 
                    : null)
                .updatedAt(data.get("updatedAt") != null 
                    ? ((com.google.cloud.Timestamp) data.get("updatedAt")).toDate().toInstant() 
                    : null)
                .build();
    }

    @Override
    public Map<String, Object> toMap(Person item) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", item.getName());
        if (item.getEmail() != null) map.put("email", item.getEmail());
        if (item.getPhone() != null) map.put("phone", item.getPhone());
        if (item.getPhotoUrl() != null) map.put("photoUrl", item.getPhotoUrl());
        if (item.getGender() != null) map.put("gender", item.getGender());
        if (item.getBirthDate() != null) map.put("birthDate", item.getBirthDate().toString());
        if (item.getComputedAgeGroup() != null) map.put("computedAgeGroup", item.getComputedAgeGroup());
        map.put("roles", item.getRoles() != null ? item.getRoles() : List.of());
        map.put("status", item.getStatus());
        map.put("createdAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        map.put("updatedAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        return map;
    }

    /**
     * Lista pessoas por role.
     */
    public List<Person> listByRole(String role) throws ExecutionException, InterruptedException {
        Query query = getCollection()
                .whereArrayContains("roles", role)
                .orderBy("name");
        QuerySnapshot snapshot = query.get().get();
        List<Person> items = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            items.add(fromDocument(doc));
        }
        return items;
    }

    /**
     * Busca pessoa por email.
     */
    public Person findByEmail(String email) throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = getCollection()
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .get();
        if (snapshot.getDocuments().isEmpty()) {
            return null;
        }
        return fromDocument(snapshot.getDocuments().get(0));
    }
}
