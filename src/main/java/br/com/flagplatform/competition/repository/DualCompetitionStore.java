package br.com.flagplatform.competition.repository;

import br.com.flagplatform.common.enums.CompetitionStatus;
import br.com.flagplatform.competition.entity.CompetitionEntity;
import br.com.flagplatform.competition.firestore.CompetitionFirestoreMapper;
import br.com.flagplatform.competition.firestore.CompetitionFirestoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação <b>dual</b> da porta {@link CompetitionStore} (ADR-006), vigente
 * quando {@code app.firestore.competition=true}: escrita dupla no PostgreSQL/JPA
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
@ConditionalOnProperty(name = "app.firestore.competition", havingValue = "true")
public class DualCompetitionStore implements CompetitionStore {

    private final CompetitionRepository jpaRepository;
    private final CompetitionFirestoreRepository firestoreRepository;
    private final CompetitionFirestoreMapper mapper;

    @Override
    public CompetitionEntity save(CompetitionEntity entity) {
        CompetitionEntity saved = jpaRepository.saveAndFlush(entity);
        firestoreRepository.save(mapper.toDocument(saved));
        log.debug("Competition persistido em dual store (id={})", saved.getId());
        return saved;
    }

    @Override
    public Optional<CompetitionEntity> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<CompetitionEntity> findAllById(Collection<UUID> ids) {
        return jpaRepository.findAllById(ids);
    }

    @Override
    public boolean existsByOrganizationIdAndNameIgnoreCase(UUID organizationId, String name) {
        return jpaRepository.existsByOrganizationIdAndNameIgnoreCase(organizationId, name);
    }

    @Override
    public boolean existsByOrganizationIdAndNameIgnoreCaseAndIdNot(UUID organizationId, String name, UUID id) {
        return jpaRepository.existsByOrganizationIdAndNameIgnoreCaseAndIdNot(organizationId, name, id);
    }

    @Override
    public List<CompetitionEntity> findAllByOrganizationIdOrderByNameAsc(UUID organizationId) {
        return jpaRepository.findAllByOrganizationIdOrderByNameAsc(organizationId);
    }

    @Override
    public Page<CompetitionEntity> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<CompetitionEntity> findAllByStatusNot(CompetitionStatus status, Pageable pageable) {
        return jpaRepository.findAllByStatusNot(status, pageable);
    }

}