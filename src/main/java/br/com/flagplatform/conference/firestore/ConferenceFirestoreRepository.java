package br.com.flagplatform.conference.firestore;

import br.com.flagplatform.common.firebase.FirestoreRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Implementação Firestore do domínio Conference (ADR-006) — espelho de escrita da
 * persistência dual (issue #10, seguindo o padrão de Organization/issue #7, Venue/issue #8
 * e Competition/issue #9). Ativada apenas quando {@code app.firestore.conference=true}
 * (e, por consequência, {@code app.firestore.enabled=true} para existir o bean
 * {@link Firestore} do {@code FirestoreFactory}).
 *
 * <p>Este repositório é o <b>espelho de escrita</b> no Firestore: as leituras do
 * fluxo REST continuam no PostgreSQL/JPA (fonte autoritativa) e a escrita é dupla
 * (JPA + Firestore) — ver {@code DualConferenceStore}. O tipo da porta é o
 * {@link ConferenceFirestoreDocument} (mapa próprio do domínio), nunca a entidade JPA.
 *
 * <p>Publica todos os dados da conferência na coleção {@code conferences}
 * (modelagem flat, ADR-006); id do documento = {@code UUID.toString()}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.conference", havingValue = "true")
public class ConferenceFirestoreRepository implements FirestoreRepository<ConferenceFirestoreDocument> {

    public static final String COLLECTION = "conferences";

    private static final long OPERATION_TIMEOUT_SECONDS = 30;

    private final Firestore firestore;

    @Override
    public Optional<ConferenceFirestoreDocument> findById(String id) {
        DocumentSnapshot snapshot = await(firestore.collection(COLLECTION).document(id).get(), "ler");
        if (!snapshot.exists()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ConferenceFirestoreDocument.fromMap(snapshot.getData()));
    }

    @Override
    public List<ConferenceFirestoreDocument> findAll() {
        QuerySnapshot snapshots = await(firestore.collection(COLLECTION).get(), "listar");
        return snapshots.getDocuments().stream()
                .map(DocumentSnapshot::getData)
                .map(ConferenceFirestoreDocument::fromMap)
                .toList();
    }

    @Override
    public ConferenceFirestoreDocument save(ConferenceFirestoreDocument document) {
        Objects.requireNonNull(document, "document não pode ser nulo");
        Objects.requireNonNull(document.id(), "id do documento não pode ser nulo");
        DocumentReference reference = firestore.collection(COLLECTION).document(document.id());
        await(reference.set(document.toMap()), "gravar");
        log.debug("Conference espelhada no Firestore (id={})", document.id());
        return document;
    }

    @Override
    public void delete(String id) {
        await(firestore.collection(COLLECTION).document(id).delete(), "remover");
    }

    private <T> T await(ApiFuture<T> future, String operation) {
        try {
            return future.get(OPERATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrompido ao " + operation + " no Firestore (coleção conferences)", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new IllegalStateException(
                    "Falha ao " + operation + " no Firestore (coleção conferences)", e);
        }
    }
}