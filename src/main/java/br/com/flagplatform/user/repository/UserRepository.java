package br.com.flagplatform.user.repository;

import br.com.flagplatform.common.model.User;
import br.com.flagplatform.common.repository.BaseFirestoreRepository;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository para usuários.
 * 
 * Acessa a collection 'users' no Firestore.
 */
@Repository
public class UserRepository extends BaseFirestoreRepository<User> {

    public UserRepository(Firestore firestore) {
        super(firestore);
    }

    @Override
    public String getCollectionName() {
        return "users";
    }

    @Override
    public User fromDocument(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        if (data == null) {
            return null;
        }

        return User.builder()
                .id(doc.getId())
                .personId((String) data.get("personId"))
                .email((String) data.get("email"))
                .passwordHash((String) data.get("passwordHash"))
                .role((String) data.get("role"))
                .status((String) data.get("status"))
                .build();
    }

    @Override
    public Map<String, Object> toMap(User item) {
        Map<String, Object> map = new HashMap<>();
        map.put("personId", item.getPersonId());
        map.put("email", item.getEmail());
        map.put("passwordHash", item.getPasswordHash());
        map.put("role", item.getRole());
        map.put("status", item.getStatus());
        map.put("createdAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        map.put("updatedAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        return map;
    }
}
