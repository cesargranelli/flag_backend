package br.com.flagplatform.venue.firestore;

import br.com.flagplatform.venue.entity.VenueEntity;
import br.com.flagplatform.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Backfill one-shot do domínio Venue (ADR-006): copia os venues do PostgreSQL
 * (fonte autoritativa) para o Firestore de forma <b>idempotente</b> — seguindo o
 * piloto Organization (issue #7). Ativado apenas quando {@code app.firestore.venue=true}
 * (perfil dev).
 *
 * <p>Regra por venue (comparando o documento atual do Firestore com o esperado):
 * <ul>
 *   <li>documento inexistente → cria;</li>
 *   <li>documento diferente → sobrescreve (atualiza);</li>
 *   <li>documento idêntico → ignora (não duplica nem regrava).</li>
 * </ul>
 *
 * <p>Pode rodar repetidas vezes sem efeito colateral; ao final registra a contagem
 * {@code backfill venues: X criadas, Y atualizadas, Z ignoradas}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.venue", havingValue = "true")
public class VenueFirestoreBackfill implements ApplicationRunner {

    private static final int PAGE_SIZE = 500;

    private final VenueRepository jpaRepository;
    private final VenueFirestoreRepository firestoreRepository;
    private final VenueFirestoreMapper mapper;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, VenueFirestoreDocument> existing = firestoreRepository.findAll().stream()
                .collect(Collectors.toMap(VenueFirestoreDocument::id, document -> document));

        int created = 0;
        int updated = 0;
        int ignored = 0;

        int page = 0;
        Page<VenueEntity> result;
        do {
            result = jpaRepository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id")));
            for (VenueEntity entity : result.getContent()) {
                VenueFirestoreDocument expected = mapper.toDocument(entity);
                VenueFirestoreDocument current = existing.get(entity.getId().toString());
                if (current == null) {
                    firestoreRepository.save(expected);
                    created++;
                } else if (expected.equals(current)) {
                    ignored++;
                } else {
                    firestoreRepository.save(expected);
                    updated++;
                }
            }
            page++;
        } while (result.hasNext());

        log.info("backfill venues: {} criadas, {} atualizadas, {} ignoradas", created, updated, ignored);
    }
}