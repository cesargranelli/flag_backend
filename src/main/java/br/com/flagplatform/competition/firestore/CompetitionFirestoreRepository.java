package br.com.flagplatform.competition.firestore;

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
 * Implementação Firestore do domínio Competition (ADR-006) — espelho de escrita da
 * persistência dual (issue #9, seguindo o piloto Organization da issue #7 e Venue da
 * issue #8). Ativada apenas quando {@code app.firestore.competition=true} (e, por
 * consequência, {@code app.firestore.enabled=true} para existir o bean {@link Firestore}
 * do {@code FirestoreFactory}).
 *
 * <p>Este repositório é o <b>espelho de escrita</b> no Firestore: as leituras do
 * fluxo REST continuam no PostgreSQL/JPA (fonte autoritativa) e a escrita é dupla
 * (JPA + Firestore) — ver {@code DualCompetitionStore}. O tipo da porta é o
 * {@link CompetitionFirestoreDocument} (mapa próprio do domínio), nunca a entidade JPA.
 *
 * <p>Publica todos os dados do campeonato na coleção {@code competitions}; id do
 * documento = {@code UUID.toString()}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.competition", havingValue = "true")
public class CompetitionFirestoreRepository implements FirestoreRepository<CompetitionFirestoreDocument> {

    public static final String COLLECTION = "competitions";

    private static final long OPERATION_TIMEOUT_SECONDS = 30;

    private final Firestore firestore;

    @Override
    public Optional<CompetitionFirestoreDocument> findById(String id) {
        DocumentSnapshot snapshot = await(firestore.collection(COLLECTION).document(id).get(), "ler");
        if (!snapshot.exists()) {
            return Optional.empty();
        }
        return Optional.ofNullable(CompetitionFirestoreDocument.fromMap(snapshot.getData()));
    }

    @Override
    public List<CompetitionFirestoreDocument> findAll() {
        QuerySnapshot snapshots = await(firestore.collection(COLLECTION).get(), "listar");
        return snapshots.getDocuments().stream()
                .map(DocumentSnapshot::getData)
                .map(CompetitionFirestoreDocument::fromMap)
                .toList();
    }

    @Override
    public CompetitionFirestoreDocument save(CompetitionFirestoreDocument document) {
        Objects.requireNonNull(document, "document não pode ser nulo");
        Objects.requireNonNull(document.id(), "id do documento não pode ser nulo");
        DocumentReference reference = firestore.collection(COLLECTION).document(document.id());
        await(reference.set(document.toMap()), "gravar");
        log.debug("Competition espelhado no Firestore (id={})", document.id());
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
                    "Interrompido ao " + operation + " no Firestore (coleção competitions)", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new IllegalStateException(
                    "Falha ao " + operation + " no Firestore (coleção competitions)", e);
        }
    }
}