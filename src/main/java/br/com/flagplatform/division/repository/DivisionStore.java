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
 *
 * <p>Exclusão é lógica (soft delete): marca {@code deletedAt} no JPA; espelha a
 * deleção no Firestore (a coleção mantém histórico).
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

    /**
     * Soft delete: marca a divisão como excluída (deletedAt). Não remove o registro.
     */
    void delete(DivisionEntity entity);

    /**
     * Soft delete em lote (mesma semântica de {@link #delete(DivisionEntity)}).
     */
    void deleteAll(Iterable<DivisionEntity> entities);

}
