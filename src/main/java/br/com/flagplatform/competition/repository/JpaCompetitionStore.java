package br.com.flagplatform.competition.repository;

import br.com.flagplatform.common.enums.CompetitionStatus;
import br.com.flagplatform.competition.entity.CompetitionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação padrão (default) da porta {@link CompetitionStore}: delega
 * integralmente ao repositório JPA/PostgreSQL atual. Vigora enquanto
 * {@code app.firestore.competition} estiver {@code false} (ou ausente) — ou seja,
 * comportamento 100% igual ao anterior à migração (ADR-006).
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.competition", havingValue = "false", matchIfMissing = true)
public class JpaCompetitionStore implements CompetitionStore {

    private final CompetitionRepository repository;

    @Override
    public CompetitionEntity save(CompetitionEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<CompetitionEntity> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<CompetitionEntity> findAllById(Collection<UUID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public boolean existsByOrganizationIdAndNameIgnoreCase(UUID organizationId, String name) {
        return repository.existsByOrganizationIdAndNameIgnoreCase(organizationId, name);
    }

    @Override
    public boolean existsByOrganizationIdAndNameIgnoreCaseAndIdNot(UUID organizationId, String name, UUID id) {
        return repository.existsByOrganizationIdAndNameIgnoreCaseAndIdNot(organizationId, name, id);
    }

    @Override
    public List<CompetitionEntity> findAllByOrganizationIdOrderByNameAsc(UUID organizationId) {
        return repository.findAllByOrganizationIdOrderByNameAsc(organizationId);
    }

    @Override
    public Page<CompetitionEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<CompetitionEntity> findAllByStatusNot(CompetitionStatus status, Pageable pageable) {
        return repository.findAllByStatusNot(status, pageable);
    }

}