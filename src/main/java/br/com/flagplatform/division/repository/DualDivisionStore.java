package br.com.flagplatform.division.repository;

import br.com.flagplatform.division.entity.DivisionEntity;
import br.com.flagplatform.division.firestore.DivisionFirestoreMapper;
import br.com.flagplatform.division.firestore.DivisionFirestoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação <b>dual</b> da porta {@link DivisionStore} (ADR-006), vigente
 * quando {@code app.firestore.division=true}: escrita dupla no PostgreSQL/JPA
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
@ConditionalOnProperty(name = "app.firestore.division", havingValue = "true")
public class DualDivisionStore implements DivisionStore {

    private final DivisionRepository jpaRepository;
    private final DivisionFirestoreRepository firestoreRepository;
    private final DivisionFirestoreMapper mapper;

    @Override
    public DivisionEntity save(DivisionEntity entity) {
        DivisionEntity saved = jpaRepository.saveAndFlush(entity);
        firestoreRepository.save(mapper.toDocument(saved));
        log.debug("Division persistida em dual store (id={})", saved.getId());
        return saved;
    }

    @Override
    public Optional<DivisionEntity> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<DivisionEntity> findAllByCompetitionIdOrderByNameAsc(UUID competitionId) {
        return jpaRepository.findAllByCompetitionIdOrderByNameAsc(competitionId);
    }

    @Override
    public List<DivisionEntity> findAllByConferenceId(UUID conferenceId) {
        return jpaRepository.findAllByConferenceId(conferenceId);
    }

    @Override
    public boolean existsByCompetitionIdAndConferenceIdAndNameIgnoreCase(
            UUID competitionId, UUID conferenceId, String name) {
        return jpaRepository.existsByCompetitionIdAndConferenceIdAndNameIgnoreCase(competitionId, conferenceId, name);
    }

    @Override
    public boolean existsByCompetitionIdAndConferenceIdAndNameIgnoreCaseAndIdNot(
            UUID competitionId, UUID conferenceId, String name, UUID id) {
        return jpaRepository.existsByCompetitionIdAndConferenceIdAndNameIgnoreCaseAndIdNot(
                competitionId, conferenceId, name, id);
    }

    @Override
    public boolean existsByCompetitionIdAndConferenceIdIsNullAndNameIgnoreCase(UUID competitionId, String name) {
        return jpaRepository.existsByCompetitionIdAndConferenceIdIsNullAndNameIgnoreCase(competitionId, name);
    }

    @Override
    public boolean existsByCompetitionIdAndConferenceIdIsNullAndNameIgnoreCaseAndIdNot(
            UUID competitionId, String name, UUID id) {
        return jpaRepository.existsByCompetitionIdAndConferenceIdIsNullAndNameIgnoreCaseAndIdNot(
                competitionId, name, id);
    }

    @Override
    public void delete(DivisionEntity entity) {
        jpaRepository.delete(entity);
        firestoreRepository.delete(entity.getId().toString());
        log.debug("Division removida do dual store (id={})", entity.getId());
    }

    @Override
    public void deleteAll(Iterable<DivisionEntity> entities) {
        jpaRepository.deleteAll(entities);
        for (DivisionEntity entity : entities) {
            firestoreRepository.delete(entity.getId().toString());
        }
        log.debug("Division(s) removida(s) do dual store");
    }

}