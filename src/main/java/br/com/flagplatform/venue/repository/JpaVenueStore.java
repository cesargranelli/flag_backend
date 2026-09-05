package br.com.flagplatform.venue.repository;

import br.com.flagplatform.venue.entity.VenueEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação padrão (default) da porta {@link VenueStore}: delega
 * integralmente ao repositório JPA/PostgreSQL atual. Vigora enquanto
 * {@code app.firestore.venue} estiver {@code false} (ou ausente) — ou seja,
 * comportamento 100% igual ao anterior à migração (ADR-006).
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.venue", havingValue = "false", matchIfMissing = true)
public class JpaVenueStore implements VenueStore {

    private final VenueRepository repository;

    @Override
    public VenueEntity save(VenueEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<VenueEntity> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Page<VenueEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

}