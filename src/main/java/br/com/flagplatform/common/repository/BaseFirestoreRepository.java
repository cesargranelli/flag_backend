package br.com.flagplatform.common.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Repository base para operações CRUD no Firestore.
 * 
 * Fornece métodos genéricos para cada entidade.
 */
@RequiredArgsConstructor
public abstract class BaseFirestoreRepository<T> {

    protected final Firestore firestore;

    /**
     * Retorna o nome da collection no Firestore.
     */
    public abstract String getCollectionName();

    /**
     * Converte DocumentSnapshot para model T.
     */
    public abstract T fromDocument(DocumentSnapshot doc);

    /**
     * Converte model T para Map (Firestore).
     */
    public abstract Map<String, Object> toMap(T item);

    /**
     * Reference da collection.
     */
    protected CollectionReference getCollection() {
        return firestore.collection(getCollectionName());
    }

    /**
     * Lista todos os documentos (com paginação).
     */
    public List<T> list(int limit) throws ExecutionException, InterruptedException {
        Query query = getCollection()
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit);
        QuerySnapshot snapshot = query.get().get();
        List<T> items = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            items.add(fromDocument(doc));
        }
        return items;
    }

    /**
     * Lista documentos com filtro.
     */
    public List<T> listWhere(String field, Object value, int limit) 
            throws ExecutionException, InterruptedException {
        Query query = getCollection()
                .whereEqualTo(field, value)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit);
        QuerySnapshot snapshot = query.get().get();
        List<T> items = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            items.add(fromDocument(doc));
        }
        return items;
    }

    /**
     * Busca documento por ID.
     */
    public T getById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = getCollection().document(id).get().get();
        if (!doc.exists()) {
            return null;
        }
        return fromDocument(doc);
    }

    /**
     * Cria novo documento.
     */
    public T create(T item) throws ExecutionException, InterruptedException {
        Map<String, Object> data = toMap(item);
        DocumentReference docRef = getCollection().add(data).get();
        return fromDocument(docRef.get().get());
    }

    /**
     * Cria novo documento com ID específico.
     */
    public T createWithId(String id, T item) throws ExecutionException, InterruptedException {
        Map<String, Object> data = toMap(item);
        getCollection().document(id).set(data).get();
        return fromDocument(getCollection().document(id).get().get());
    }

    /**
     * Atualiza documento existente.
     */
    public T update(String id, Map<String, Object> data) throws ExecutionException, InterruptedException {
        data.put("updatedAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        getCollection().document(id).update(data).get();
        return getById(id);
    }

    /**
     * Remove documento.
     */
    public void delete(String id) throws ExecutionException, InterruptedException {
        getCollection().document(id).delete().get();
    }

    /**
     * Lista todos os documentos.
     */
    public List<T> listAll() throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = getCollection()
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .get();
        List<T> items = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            items.add(fromDocument(doc));
        }
        return items;
    }
}
