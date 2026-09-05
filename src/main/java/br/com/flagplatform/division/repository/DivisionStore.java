package br.com.flagplatform.division.repository;

import br.com.flagplatform.division.entity.DivisionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de persistência do domínio Division (ADR-006). Isola o
 * {@code DivisionService} (e o cascade de exclusão do {@code ConferenceService})
 * da tecnologia de armazenamento e viabiliza a <b>persistência dual</b>: com
 * {@code app.firestore.division=false} vigora a implementação JPA/PostgreSQL
 * ({@link JpaDivisionStore}, padrão atual); com a flag {@code true} entra
 * {@link DualDivisionStore}, que mantém o PostgreSQL como escrita autoritativa e
 * espelha toda escrita no Firestore.
 *
 * <p>As leituras sempre vêm do PostgreSQL (fonte de verdade); o Firestore é o
 * espelho/realtime para os apps. Regras de negócio e contrato REST ficam intactos
 * no service — ele conhece apenas esta porta.
 */
public interface DivisionStore {

    DivisionEntity save(DivisionEntity entity);

    Optional<DivisionEntity> findById(UUID id);

    List<DivisionEntity> findAllByCompetitionIdOrderByNameAsc(UUID competitionId);

    List<DivisionEntity> findAllByConferenceId(UUID conferenceId);

    boolean existsByCompetitionIdAndConferenceIdAndNameIgnoreCase(
            UUID competitionId, UUID conferenceId, String name);

    boolean existsByCompetitionIdAndConferenceIdAndNameIgnoreCaseAndIdNot(
            UUID competitionId, UUID conferenceId, String name, UUID id);

    boolean existsByCompetitionIdAndConferenceIdIsNullAndNameIgnoreCase(UUID competitionId, String name);

    boolean existsByCompetitionIdAndConferenceIdIsNullAndNameIgnoreCaseAndIdNot(
            UUID competitionId, String name, UUID id);

    void delete(DivisionEntity entity);

    void deleteAll(Iterable<DivisionEntity> entities);

}