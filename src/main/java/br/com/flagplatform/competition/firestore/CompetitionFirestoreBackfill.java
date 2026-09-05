package br.com.flagplatform.competition.firestore;

import br.com.flagplatform.competition.entity.CompetitionEntity;
import br.com.flagplatform.competition.repository.CompetitionRepository;
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
 * Backfill one-shot do domínio Competition (ADR-006): copia os campeonatos do
 * PostgreSQL (fonte autoritativa) para o Firestore de forma <b>idempotente</b> —
 * seguindo o piloto Organization (issue #7) e Venue (issue #8). Ativado apenas
 * quando {@code app.firestore.competition=true} (perfil dev).
 *
 * <p>Regra por campeonato (comparando o documento atual do Firestore com o esperado):
 * <ul>
 *   <li>documento inexistente → cria;</li>
 *   <li>documento diferente → sobrescreve (atualiza);</li>
 *   <li>documento idêntico → ignora (não duplica nem regrava).</li>
 * </ul>
 *
 * <p>Pode rodar repetidas vezes sem efeito colateral; ao final registra a contagem
 * {@code backfill competitions: X criadas, Y atualizadas, Z ignoradas}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.competition", havingValue = "true")
public class CompetitionFirestoreBackfill implements ApplicationRunner {

    private static final int PAGE_SIZE = 500;

    private final CompetitionRepository jpaRepository;
    private final CompetitionFirestoreRepository firestoreRepository;
    private final CompetitionFirestoreMapper mapper;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, CompetitionFirestoreDocument> existing = firestoreRepository.findAll().stream()
                .collect(Collectors.toMap(CompetitionFirestoreDocument::id, document -> document));

        int created = 0;
        int updated = 0;
        int ignored = 0;

        int page = 0;
        Page<CompetitionEntity> result;
        do {
            result = jpaRepository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id")));
            for (CompetitionEntity entity : result.getContent()) {
                CompetitionFirestoreDocument expected = mapper.toDocument(entity);
                CompetitionFirestoreDocument current = existing.get(entity.getId().toString());
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

        log.info("backfill competitions: {} criadas, {} atualizadas, {} ignoradas", created, updated, ignored);
    }
}