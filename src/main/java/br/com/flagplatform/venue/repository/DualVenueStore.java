package br.com.flagplatform.venue.repository;

import br.com.flagplatform.venue.entity.VenueEntity;
import br.com.flagplatform.venue.firestore.VenueFirestoreMapper;
import br.com.flagplatform.venue.firestore.VenueFirestoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação <b>dual</b> da porta {@link VenueStore} (ADR-006), vigente
 * quando {@code app.firestore.venue=true}: escrita dupla no PostgreSQL/JPA
 * (autoritativa) e no Firestore (espelho para os apps), leitura sempre no JPA.
 *
 * <p>A escrita JPA usa {@code saveAndFlush} para materializar {@code id},
 * {@code createdAt} e {@code updatedAt} (callbacks {@code @PrePersist}/{@code @PreUpdate})
 * antes de espelhar no Firestore — o documento espelho reflete exatamente o que o
 * Postgres registrou. Se o Firestore falhar, a exceção propaga e a transação JPA
 * faz rollback (persistência dual fail-fast: ou grava nas duas, ou em nenhuma).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.venue", havingValue = "true")
public class DualVenueStore implements VenueStore {

    private final VenueRepository jpaRepository;
    private final VenueFirestoreRepository firestoreRepository;
    private final VenueFirestoreMapper mapper;

    @Override
    public VenueEntity save(VenueEntity entity) {
        VenueEntity saved = jpaRepository.saveAndFlush(entity);
        firestoreRepository.save(mapper.toDocument(saved));
        log.debug("Venue persistido em dual store (id={})", saved.getId());
        return saved;
    }

    @Override
    public Optional<VenueEntity> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Page<VenueEntity> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

}