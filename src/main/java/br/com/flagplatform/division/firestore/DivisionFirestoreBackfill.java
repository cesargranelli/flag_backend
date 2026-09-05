package br.com.flagplatform.division.firestore;

import br.com.flagplatform.division.entity.DivisionEntity;
import br.com.flagplatform.division.repository.DivisionRepository;
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
 * Backfill one-shot do domínio Division (ADR-006): copia as divisões do
 * PostgreSQL (fonte autoritativa) para o Firestore de forma <b>idempotente</b> —
 * seguindo o padrão de Organization (issue #7), Venue (issue #8) e Competition
 * (issue #9). Ativado apenas quando {@code app.firestore.division=true} (perfil dev).
 *
 * <p>Regra por divisão (comparando o documento atual do Firestore com o esperado):
 * <ul>
 *   <li>documento inexistente → cria;</li>
 *   <li>documento diferente → sobrescreve (atualiza);</li>
 *   <li>documento idêntico → ignora (não duplica nem regrava).</li>
 * </ul>
 *
 * <p>Pode rodar repetidas vezes sem efeito colateral; ao final registra a contagem
 * {@code backfill divisions: X criadas, Y atualizadas, Z ignoradas}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.division", havingValue = "true")
public class DivisionFirestoreBackfill implements ApplicationRunner {

    private static final int PAGE_SIZE = 500;

    private final DivisionRepository jpaRepository;
    private final DivisionFirestoreRepository firestoreRepository;
    private final DivisionFirestoreMapper mapper;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, DivisionFirestoreDocument> existing = firestoreRepository.findAll().stream()
                .collect(Collectors.toMap(DivisionFirestoreDocument::id, document -> document));

        int created = 0;
        int updated = 0;
        int ignored = 0;

        int page = 0;
        Page<DivisionEntity> result;
        do {
            result = jpaRepository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id")));
            for (DivisionEntity entity : result.getContent()) {
                DivisionFirestoreDocument expected = mapper.toDocument(entity);
                DivisionFirestoreDocument current = existing.get(entity.getId().toString());
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

        log.info("backfill divisions: {} criadas, {} atualizadas, {} ignoradas", created, updated, ignored);
    }
}