package br.com.flagplatform.conference.repository;

import br.com.flagplatform.conference.entity.ConferenceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação padrão (default) da porta {@link ConferenceStore}: delega
 * integralmente ao repositório JPA/PostgreSQL atual. Vigora enquanto
 * {@code app.firestore.conference} estiver {@code false} (ou ausente) — ou seja,
 * comportamento 100% igual ao anterior à migração (ADR-006).
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.conference", havingValue = "false", matchIfMissing = true)
public class JpaConferenceStore implements ConferenceStore {

    private final ConferenceRepository repository;

    @Override
    public ConferenceEntity save(ConferenceEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<ConferenceEntity> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<ConferenceEntity> findAllByCompetitionIdOrderByNameAsc(UUID competitionId) {
        return repository.findAllByCompetitionIdOrderByNameAsc(competitionId);
    }

    @Override
    public boolean existsByCompetitionIdAndNameIgnoreCase(UUID competitionId, String name) {
        return repository.existsByCompetitionIdAndNameIgnoreCase(competitionId, name);
    }

    @Override
    public boolean existsByCompetitionIdAndNameIgnoreCaseAndIdNot(UUID competitionId, String name, UUID id) {
        return repository.existsByCompetitionIdAndNameIgnoreCaseAndIdNot(competitionId, name, id);
    }

    @Override
    public void delete(ConferenceEntity entity) {
        repository.delete(entity);
    }

}