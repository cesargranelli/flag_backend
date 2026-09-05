package br.com.flagplatform.competition.repository;

import br.com.flagplatform.common.enums.CompetitionStatus;
import br.com.flagplatform.competition.entity.CompetitionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de persistência do domínio Competition (ADR-006). Isola o
 * {@code CompetitionService} da tecnologia de armazenamento e viabiliza a
 * <b>persistência dual</b>: com {@code app.firestore.competition=false} vigora a
 * implementação JPA/PostgreSQL ({@link JpaCompetitionStore}, padrão atual); com a
 * flag {@code true} entra {@link DualCompetitionStore}, que mantém o PostgreSQL
 * como escrita autoritativa e espelha toda escrita no Firestore.
 *
 * <p>As leituras sempre vêm do PostgreSQL (fonte de verdade); o Firestore é o
 * espelho/realtime para os apps. Regras de negócio e contrato REST ficam intactos
 * no service — ele conhece apenas esta porta.
 */
public interface CompetitionStore {

    CompetitionEntity save(CompetitionEntity entity);

    Optional<CompetitionEntity> findById(UUID id);

    List<CompetitionEntity> findAllById(Collection<UUID> ids);

    boolean existsByOrganizationIdAndNameIgnoreCase(UUID organizationId, String name);

    boolean existsByOrganizationIdAndNameIgnoreCaseAndIdNot(UUID organizationId, String name, UUID id);

    List<CompetitionEntity> findAllByOrganizationIdOrderByNameAsc(UUID organizationId);

    Page<CompetitionEntity> findAll(Pageable pageable);

    Page<CompetitionEntity> findAllByStatusNot(CompetitionStatus status, Pageable pageable);

}