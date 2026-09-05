package br.com.flagplatform.division.repository;

import br.com.flagplatform.division.entity.DivisionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação padrão (default) da porta {@link DivisionStore}: delega
 * integralmente ao repositório JPA/PostgreSQL atual. Vigora enquanto
 * {@code app.firestore.division} estiver {@code false} (ou ausente) — ou seja,
 * comportamento 100% igual ao anterior à migração (ADR-006).
 *
 * <p>A exclusão é lógica (soft delete): chama {@code softDeleteById} no JPA
 * (marca {@code deletedAt}).
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.division", havingValue = "false", matchIfMissing = true)
public class JpaDivisionStore implements DivisionStore {

    private final DivisionRepository repository;

    @Override
    public DivisionEntity save(DivisionEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<DivisionEntity> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<DivisionEntity> findAllByCompetitionIdOrderByNameAsc(UUID competitionId) {
        return repository.findAllByCompetitionIdOrderByNameAsc(competitionId);
    }

    @Override
    public List<DivisionEntity> findAllByConferenceId(UUID conferenceId) {
        return repository.findAllByConferenceId(conferenceId);
    }

    @Override
    public boolean existsByCompetitionIdAndConferenceIdAndNameIgnoreCase(
            UUID competitionId, UUID conferenceId, String name) {
        return repository.existsByCompetitionIdAndConferenceIdAndNameIgnoreCase(competitionId, conferenceId, name);
    }

    @Override
    public boolean existsByCompetitionIdAndConferenceIdAndNameIgnoreCaseAndIdNot(
            UUID competitionId, UUID conferenceId, String name, UUID id) {
        return repository.existsByCompetitionIdAndConferenceIdAndNameIgnoreCaseAndIdNot(
                competitionId, conferenceId, name, id);
    }

    @Override
    public boolean existsByCompetitionIdAndConferenceIdIsNullAndNameIgnoreCase(UUID competitionId, String name) {
        return repository.existsByCompetitionIdAndConferenceIdIsNullAndNameIgnoreCase(competitionId, name);
    }

    @Override
    public boolean existsByCompetitionIdAndConferenceIdIsNullAndNameIgnoreCaseAndIdNot(
            UUID competitionId, String name, UUID id) {
        return repository.existsByCompetitionIdAndConferenceIdIsNullAndNameIgnoreCaseAndIdNot(
                competitionId, name, id);
    }

    @Override
    public void delete(DivisionEntity entity) {
        repository.softDeleteById(entity.getId());
    }

    @Override
    public void deleteAll(Iterable<DivisionEntity> entities) {
        entities.forEach(e -> repository.softDeleteById(e.getId()));
    }

}
