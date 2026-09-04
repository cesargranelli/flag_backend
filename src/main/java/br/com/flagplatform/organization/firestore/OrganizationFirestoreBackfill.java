package br.com.flagplatform.organization.firestore;

import br.com.flagplatform.organization.entity.OrganizationEntity;
import br.com.flagplatform.organization.repository.OrganizationRepository;
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
 * Backfill one-shot do domínio Organization (ADR-006): copia as organizações do
 * PostgreSQL (fonte autoritativa) para o Firestore de forma <b>idempotente</b>.
 * Ativado apenas quando {@code app.firestore.organization=true} (perfil dev).
 *
 * <p>Regra por organização (comparando o documento atual do Firestore com o esperado):
 * <ul>
 *   <li>documento inexistente → cria;</li>
 *   <li>documento diferente → sobrescreve (atualiza);</li>
 *   <li>documento idêntico → ignora (não duplica nem regrava).</li>
 * </ul>
 *
 * <p>Pode rodar repetidas vezes sem efeito colateral; ao final registra a contagem
 * {@code backfill organizations: X criadas, Y atualizadas, Z ignoradas}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.organization", havingValue = "true")
public class OrganizationFirestoreBackfill implements ApplicationRunner {

    private static final int PAGE_SIZE = 500;

    private final OrganizationRepository jpaRepository;
    private final OrganizationFirestoreRepository firestoreRepository;
    private final OrganizationFirestoreMapper mapper;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, OrganizationFirestoreDocument> existing = firestoreRepository.findAll().stream()
                .collect(Collectors.toMap(OrganizationFirestoreDocument::id, document -> document));

        int created = 0;
        int updated = 0;
        int ignored = 0;

        int page = 0;
        Page<OrganizationEntity> result;
        do {
            result = jpaRepository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id")));
            for (OrganizationEntity entity : result.getContent()) {
                OrganizationFirestoreDocument expected = mapper.toDocument(entity);
                OrganizationFirestoreDocument current = existing.get(entity.getId().toString());
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

        log.info("backfill organizations: {} criadas, {} atualizadas, {} ignoradas", created, updated, ignored);
    }
}