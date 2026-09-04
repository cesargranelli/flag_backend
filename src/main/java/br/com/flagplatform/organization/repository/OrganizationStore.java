package br.com.flagplatform.organization.repository;

import br.com.flagplatform.common.enums.OrganizationStatus;
import br.com.flagplatform.common.enums.OrganizationType;
import br.com.flagplatform.organization.entity.OrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de persistência do domínio Organization (ADR-006). Isola o
 * {@code OrganizationService} da tecnologia de armazenamento e viabiliza a
 * <b>persistência dual</b>: com {@code app.firestore.organization=false} vigora a
 * implementação JPA/PostgreSQL ({@link JpaOrganizationStore}, padrão atual); com a
 * flag {@code true} entra {@link DualOrganizationStore}, que mantém o PostgreSQL
 * como escrita autoritativa e espelha toda escrita no Firestore.
 *
 * <p>As leituras sempre vêm do PostgreSQL (fonte de verdade); o Firestore é o
 * espelho/realtime para os apps. Regras de negócio e contrato REST ficam intactos
 * no service — ele conhece apenas esta porta.
 */
public interface OrganizationStore {

    OrganizationEntity save(OrganizationEntity entity);

    Optional<OrganizationEntity> findById(UUID id);

    boolean existsByTradeNameIgnoreCase(String tradeName);

    boolean existsByTradeNameIgnoreCaseAndIdNot(String tradeName, UUID id);

    boolean existsByDocument(String document);

    boolean existsByDocumentAndIdNot(String document, UUID id);

    Page<OrganizationEntity> findAll(Pageable pageable);

    Page<OrganizationEntity> findAllByStatus(OrganizationStatus status, Pageable pageable);

    List<OrganizationEntity> findAllByParentIdAndOrganizationTypeInOrderByTradeNameAsc(
            UUID parentId, Collection<OrganizationType> organizationTypes);

}