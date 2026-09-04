package br.com.flagplatform.organization.repository;

import br.com.flagplatform.common.enums.OrganizationStatus;
import br.com.flagplatform.common.enums.OrganizationType;
import br.com.flagplatform.organization.entity.OrganizationEntity;
import br.com.flagplatform.organization.firestore.OrganizationFirestoreMapper;
import br.com.flagplatform.organization.firestore.OrganizationFirestoreRepository;
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
 * Implementação <b>dual</b> da porta {@link OrganizationStore} (ADR-006), vigente
 * quando {@code app.firestore.organization=true}: escrita dupla no PostgreSQL/JPA
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
@ConditionalOnProperty(name = "app.firestore.organization", havingValue = "true")
public class DualOrganizationStore implements OrganizationStore {

    private final OrganizationRepository jpaRepository;
    private final OrganizationFirestoreRepository firestoreRepository;
    private final OrganizationFirestoreMapper mapper;

    @Override
    public OrganizationEntity save(OrganizationEntity entity) {
        OrganizationEntity saved = jpaRepository.saveAndFlush(entity);
        firestoreRepository.save(mapper.toDocument(saved));
        log.debug("Organization persistida em dual store (id={})", saved.getId());
        return saved;
    }

    @Override
    public Optional<OrganizationEntity> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsByTradeNameIgnoreCase(String tradeName) {
        return jpaRepository.existsByTradeNameIgnoreCase(tradeName);
    }

    @Override
    public boolean existsByTradeNameIgnoreCaseAndIdNot(String tradeName, UUID id) {
        return jpaRepository.existsByTradeNameIgnoreCaseAndIdNot(tradeName, id);
    }

    @Override
    public boolean existsByDocument(String document) {
        return jpaRepository.existsByDocument(document);
    }

    @Override
    public boolean existsByDocumentAndIdNot(String document, UUID id) {
        return jpaRepository.existsByDocumentAndIdNot(document, id);
    }

    @Override
    public Page<OrganizationEntity> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<OrganizationEntity> findAllByStatus(OrganizationStatus status, Pageable pageable) {
        return jpaRepository.findAllByStatus(status, pageable);
    }

    @Override
    public List<OrganizationEntity> findAllByParentIdAndOrganizationTypeInOrderByTradeNameAsc(
            UUID parentId, Collection<OrganizationType> organizationTypes) {
        return jpaRepository.findAllByParentIdAndOrganizationTypeInOrderByTradeNameAsc(parentId, organizationTypes);
    }

}