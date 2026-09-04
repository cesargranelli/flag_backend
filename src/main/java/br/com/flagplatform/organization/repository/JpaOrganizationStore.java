package br.com.flagplatform.organization.repository;

import br.com.flagplatform.common.enums.OrganizationStatus;
import br.com.flagplatform.common.enums.OrganizationType;
import br.com.flagplatform.organization.entity.OrganizationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação padrão (default) da porta {@link OrganizationStore}: delega
 * integralmente ao repositório JPA/PostgreSQL atual. Vigora enquanto
 * {@code app.firestore.organization} estiver {@code false} (ou ausente) — ou seja,
 * comportamento 100% igual ao anterior à migração (ADR-006).
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.firestore.organization", havingValue = "false", matchIfMissing = true)
public class JpaOrganizationStore implements OrganizationStore {

    private final OrganizationRepository repository;

    @Override
    public OrganizationEntity save(OrganizationEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<OrganizationEntity> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsByTradeNameIgnoreCase(String tradeName) {
        return repository.existsByTradeNameIgnoreCase(tradeName);
    }

    @Override
    public boolean existsByTradeNameIgnoreCaseAndIdNot(String tradeName, UUID id) {
        return repository.existsByTradeNameIgnoreCaseAndIdNot(tradeName, id);
    }

    @Override
    public boolean existsByDocument(String document) {
        return repository.existsByDocument(document);
    }

    @Override
    public boolean existsByDocumentAndIdNot(String document, UUID id) {
        return repository.existsByDocumentAndIdNot(document, id);
    }

    @Override
    public Page<OrganizationEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<OrganizationEntity> findAllByStatus(OrganizationStatus status, Pageable pageable) {
        return repository.findAllByStatus(status, pageable);
    }

    @Override
    public List<OrganizationEntity> findAllByParentIdAndOrganizationTypeInOrderByTradeNameAsc(
            UUID parentId, Collection<OrganizationType> organizationTypes) {
        return repository.findAllByParentIdAndOrganizationTypeInOrderByTradeNameAsc(parentId, organizationTypes);
    }

}