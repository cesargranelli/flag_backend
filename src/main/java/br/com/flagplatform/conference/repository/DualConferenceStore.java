package br.com.flagplatform.conference.repository;

import br.com.flagplatform.conference.entity.ConferenceEntity;
import br.com.flagplatform.conference.firestore.ConferenceFirestoreMapper;
import br.com.flagplatform.conference.firestore.ConferenceFirestoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação <b>dual</b> da porta {@link ConferenceStore} (ADR-006), vigente
 * quando {@code app.firestore.conference=true}: escrita dupla no PostgreSQL/JPA
 * (autoritativa) e no Firestore (espelho para os apps), leitura sempre no JPA.
 *
 * <p>A escrita JPA usa {@code saveAndFlush} para materializar {@code id},
 * {@code createdAt} e {@code updatedAt} (callbacks {@code @PrePersist}/{@code @PreUpdate})
 * antes de espelhar no Firestore — o documento espelho reflete exatamente o que o
 * Postgres registrou. Se o Firestore falhar, a exceção propaga e a transação JPA
 * faz rollback (persistência dual fail-fast: ou grava nas duas, ou em nenhuma).
 * A exclusão segue o mesmo princípio: apaga no JPA e remove o espelho no Firestore.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.conference", havingValue = "true")
public class DualConferenceStore implements ConferenceStore {

    private final ConferenceRepository jpaRepository;
    private final ConferenceFirestoreRepository firestoreRepository;
    private final ConferenceFirestoreMapper mapper;

    @Override
    public ConferenceEntity save(ConferenceEntity entity) {
        ConferenceEntity saved = jpaRepository.saveAndFlush(entity);
        firestoreRepository.save(mapper.toDocument(saved));
        log.debug("Conference persistida em dual store (id={})", saved.getId());
        return saved;
    }

    @Override
    public Optional<ConferenceEntity> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ConferenceEntity> findAllByCompetitionIdOrderByNameAsc(UUID competitionId) {
        return jpaRepository.findAllByCompetitionIdOrderByNameAsc(competitionId);
    }

    @Override
    public boolean existsByCompetitionIdAndNameIgnoreCase(UUID competitionId, String name) {
        return jpaRepository.existsByCompetitionIdAndNameIgnoreCase(competitionId, name);
    }

    @Override
    public boolean existsByCompetitionIdAndNameIgnoreCaseAndIdNot(UUID competitionId, String name, UUID id) {
        return jpaRepository.existsByCompetitionIdAndNameIgnoreCaseAndIdNot(competitionId, name, id);
    }

    @Override
    public void delete(ConferenceEntity entity) {
        jpaRepository.delete(entity);
        firestoreRepository.delete(entity.getId().toString());
        log.debug("Conference removida do dual store (id={})", entity.getId());
    }

}