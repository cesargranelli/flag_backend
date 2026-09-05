package br.com.flagplatform.conference.firestore;

import br.com.flagplatform.conference.entity.ConferenceEntity;
import br.com.flagplatform.conference.repository.ConferenceRepository;
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
 * Backfill one-shot do domínio Conference (ADR-006): copia as conferências do
 * PostgreSQL (fonte autoritativa) para o Firestore de forma <b>idempotente</b> —
 * seguindo o padrão de Organization (issue #7), Venue (issue #8) e Competition
 * (issue #9). Ativado apenas quando {@code app.firestore.conference=true} (perfil dev).
 *
 * <p>Regra por conferência (comparando o documento atual do Firestore com o esperado):
 * <ul>
 *   <li>documento inexistente → cria;</li>
 *   <li>documento diferente → sobrescreve (atualiza);</li>
 *   <li>documento idêntico → ignora (não duplica nem regrava).</li>
 * </ul>
 *
 * <p>Pode rodar repetidas vezes sem efeito colateral; ao final registra a contagem
 * {@code backfill conferences: X criadas, Y atualizadas, Z ignoradas}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.conference", havingValue = "true")
public class ConferenceFirestoreBackfill implements ApplicationRunner {

    private static final int PAGE_SIZE = 500;

    private final ConferenceRepository jpaRepository;
    private final ConferenceFirestoreRepository firestoreRepository;
    private final ConferenceFirestoreMapper mapper;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, ConferenceFirestoreDocument> existing = firestoreRepository.findAll().stream()
                .collect(Collectors.toMap(ConferenceFirestoreDocument::id, document -> document));

        int created = 0;
        int updated = 0;
        int ignored = 0;

        int page = 0;
        Page<ConferenceEntity> result;
        do {
            result = jpaRepository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id")));
            for (ConferenceEntity entity : result.getContent()) {
                ConferenceFirestoreDocument expected = mapper.toDocument(entity);
                ConferenceFirestoreDocument current = existing.get(entity.getId().toString());
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

        log.info("backfill conferences: {} criadas, {} atualizadas, {} ignoradas", created, updated, ignored);
    }
}