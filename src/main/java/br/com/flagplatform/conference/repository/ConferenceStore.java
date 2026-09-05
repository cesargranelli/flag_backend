package br.com.flagplatform.conference.repository;

import br.com.flagplatform.conference.entity.ConferenceEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de persistência do domínio Conference (ADR-006). Isola o
 * {@code ConferenceService} da tecnologia de armazenamento e viabiliza a
 * <b>persistência dual</b>: com {@code app.firestore.conference=false} vigora a
 * implementação JPA/PostgreSQL ({@link JpaConferenceStore}, padrão atual); com a
 * flag {@code true} entra {@link DualConferenceStore}, que mantém o PostgreSQL
 * como escrita autoritativa e espelha toda escrita no Firestore.
 *
 * <p>As leituras sempre vêm do PostgreSQL (fonte de verdade); o Firestore é o
 * espelho/realtime para os apps. Regras de negócio e contrato REST ficam intactos
 * no service — ele conhece apenas esta porta.
 */
public interface ConferenceStore {

    ConferenceEntity save(ConferenceEntity entity);

    Optional<ConferenceEntity> findById(UUID id);

    List<ConferenceEntity> findAllByCompetitionIdOrderByNameAsc(UUID competitionId);

    boolean existsByCompetitionIdAndNameIgnoreCase(UUID competitionId, String name);

    boolean existsByCompetitionIdAndNameIgnoreCaseAndIdNot(UUID competitionId, String name, UUID id);

    void delete(ConferenceEntity entity);

}