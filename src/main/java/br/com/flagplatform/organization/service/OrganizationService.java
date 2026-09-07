package br.com.flagplatform.organization.service;

import br.com.flagplatform.common.enums.DocumentType;
import br.com.flagplatform.common.enums.OrganizationStatus;
import br.com.flagplatform.common.enums.OrganizationType;
import br.com.flagplatform.common.exception.DuplicateDocumentException;
import br.com.flagplatform.common.exception.InvalidDocumentException;
import br.com.flagplatform.common.pagination.PagedResponse;
import br.com.flagplatform.common.validation.DocumentValidator;
import br.com.flagplatform.organization.OrganizationLookup;
import br.com.flagplatform.organization.dto.request.CreateOrganizationRequest;
import br.com.flagplatform.organization.dto.request.UpdateOrganizationRequest;
import br.com.flagplatform.organization.dto.response.OrganizationCreatedResponse;
import br.com.flagplatform.organization.dto.response.OrganizationResponse;
import br.com.flagplatform.organization.entity.OrganizationEntity;
import br.com.flagplatform.organization.exception.DuplicateTradeNameException;
import br.com.flagplatform.organization.exception.InvalidOrganizationHierarchyException;
import br.com.flagplatform.organization.exception.OrganizationAssociationConflictException;
import br.com.flagplatform.organization.exception.OrganizationAssociationNotFoundException;
import br.com.flagplatform.organization.exception.OrganizationNotFoundException;
import br.com.flagplatform.organization.mapper.OrganizationMapper;
import br.com.flagplatform.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrganizationService implements OrganizationLookup {

    /**
     * Tipos que podem atuar como organização mãe na hierarquia (ADR-006).
     */
    private static final List<OrganizationType> PARENT_TYPES = List.of(
            OrganizationType.FEDERATION,
            OrganizationType.LEAGUE,
            OrganizationType.ASSOCIATION);

    /**
     * Tipos que podem ser associados como organização filha na governança (ADR-009).
     */
    private static final List<OrganizationType> CHILD_TYPES = List.of(
            OrganizationType.LEAGUE,
            OrganizationType.ASSOCIATION);

    /**
     * Tipos válidos de organização conforme o modelo de domínio (ADR-009).
     */
    private static final List<OrganizationType> VALID_ORGANIZATION_TYPES = List.of(
            OrganizationType.values());

    private final OrganizationMapper mapper;
    private final OrganizationRepository repository;

    @Transactional
    public OrganizationCreatedResponse create(CreateOrganizationRequest request) {

        if (repository.existsByTradeNameIgnoreCase(request.tradeName())) {
            throw new DuplicateTradeNameException(request.tradeName());
        }

        validateDocument(request.document(), request.documentType(), null);
        validatePresident(request.presidentCpf());

        OrganizationEntity entity = mapper.toEntity(request);
        entity.setStatus(OrganizationStatus.ACTIVE);

        OrganizationEntity saved = repository.save(entity);

        return mapper.toResponse(saved);
    }

    @Transactional
    public OrganizationResponse update(UUID id, UpdateOrganizationRequest req) {
        OrganizationEntity entity = findEntityById(id);

        if (req.tradeName() != null && !req.tradeName().isBlank()) {
            if (repository.existsByTradeNameIgnoreCaseAndIdNot(req.tradeName().trim(), id)) {
                throw new DuplicateTradeNameException(req.tradeName().trim());
            }
            entity.setTradeName(req.tradeName().trim());
        }

        if (req.legalName() != null && !req.legalName().isBlank()) {
            entity.setLegalName(req.legalName().trim());
        }

        if (req.abbreviation() != null) {
            entity.setAbbreviation(req.abbreviation().trim().isEmpty() ? null : req.abbreviation().trim());
        }

        if (req.organizationType() != null) {
            entity.setOrganizationType(req.organizationType());
        }

        if (req.document() != null) {
            validateDocument(req.document(), req.documentType() != null ? req.documentType() : entity.getDocumentType(), id);
            entity.setDocument(req.document().trim().isEmpty() ? null : req.document().trim().replaceAll("\\D", ""));
            if (req.documentType() != null) {
                entity.setDocumentType(req.documentType());
            }
        }

        if (req.presidentName() != null && !req.presidentName().isBlank()) {
            entity.setPresidentName(req.presidentName().trim());
        }

        if (req.presidentCpf() != null && !req.presidentCpf().isBlank()) {
            validatePresident(req.presidentCpf().trim());
            entity.setPresidentCpf(req.presidentCpf().trim().replaceAll("\\D", ""));
        }

        if (req.email() != null) {
            entity.setEmail(req.email().trim().isEmpty() ? null : req.email().trim());
        }
        if (req.phone() != null) {
            entity.setPhone(req.phone().trim().isEmpty() ? null : req.phone().trim());
        }
        if (req.website() != null) {
            entity.setWebsite(req.website().trim().isEmpty() ? null : req.website().trim());
        }
        if (req.instagram() != null) {
            entity.setInstagram(req.instagram().trim().isEmpty() ? null : req.instagram().trim());
        }
        if (req.country() != null && !req.country().isBlank()) {
            entity.setCountry(req.country().trim());
        }
        if (req.state() != null) {
            entity.setState(req.state().trim().isEmpty() ? null : req.state().trim());
        }
        if (req.city() != null) {
            entity.setCity(req.city().trim().isEmpty() ? null : req.city().trim());
        }
        if (req.logoUrl() != null) {
            entity.setLogoUrl(req.logoUrl().trim().isEmpty() ? null : req.logoUrl().trim());
        }
        if (req.primaryColor() != null) {
            entity.setPrimaryColor(req.primaryColor().trim().isEmpty() ? null : req.primaryColor().trim());
        }
        if (req.secondaryColor() != null) {
            entity.setSecondaryColor(req.secondaryColor().trim().isEmpty() ? null : req.secondaryColor().trim());
        }
        if (req.tertiaryColor() != null) {
            entity.setTertiaryColor(req.tertiaryColor().trim().isEmpty() ? null : req.tertiaryColor().trim());
        }
        if (req.quaternaryColor() != null) {
            entity.setQuaternaryColor(req.quaternaryColor().trim().isEmpty() ? null : req.quaternaryColor().trim());
        }
        if (req.timezone() != null && !req.timezone().isBlank()) {
            entity.setTimezone(req.timezone().trim());
        }
        if (req.locale() != null && !req.locale().isBlank()) {
            entity.setLocale(req.locale().trim());
        }

        OrganizationEntity saved = repository.save(entity);
        return mapper.toDetailResponse(saved);
    }

    public PagedResponse<OrganizationResponse> findAll(
            int page, int size, boolean includeDisabled, boolean isAdmin) {
        boolean showAll = includeDisabled && isAdmin;

        Page<OrganizationEntity> result = showAll
                ? repository.findAllByOrganizationTypeIn(
                        VALID_ORGANIZATION_TYPES,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "tradeName")))
                : repository.findAllByStatusAndOrganizationTypeIn(
                        OrganizationStatus.ACTIVE,
                        VALID_ORGANIZATION_TYPES,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "tradeName")));

        return new PagedResponse<>(
                mapper.toDetailResponseList(result.getContent()),
                result.getTotalElements());
    }

    public OrganizationResponse findById(UUID id, boolean isAdmin) {
        OrganizationEntity entity = findEntityById(id);
        if (entity.getStatus() == OrganizationStatus.INACTIVE && !isAdmin) {
            // Desativada é visível apenas ao ADMIN (V246).
            throw new OrganizationNotFoundException(id);
        }
        return mapper.toDetailResponse(entity);
    }

    @Transactional
    public void deactivate(UUID id) {
        OrganizationEntity entity = findEntityById(id);
        entity.setStatus(OrganizationStatus.INACTIVE);
        repository.save(entity);
    }

    @Transactional
    public void reactivate(UUID id) {
        OrganizationEntity entity = findEntityById(id);
        entity.setStatus(OrganizationStatus.ACTIVE);
        repository.save(entity);
    }

    public List<OrganizationResponse> findClubs(UUID parentId) {
        findEntityById(parentId);
        return mapper.toDetailResponseList(repository
                .findAllByParentIdAndOrganizationTypeInOrderByTradeNameAsc(parentId, CHILD_TYPES));
    }

    @Transactional
    public OrganizationResponse associateClub(UUID parentId, UUID clubId) {
        OrganizationEntity parent = findEntityById(parentId);
        OrganizationEntity child = findEntityById(clubId);

        if (parentId.equals(clubId)) {
            throw new InvalidOrganizationHierarchyException(
                    "Uma organização não pode ser associada a si mesma.");
        }
        if (!PARENT_TYPES.contains(parent.getOrganizationType())) {
            throw new InvalidOrganizationHierarchyException(
                    "A organização pai deve ser FEDERAÇÃO, LIGA ou ASSOCIAÇÃO.");
        }
        if (!CHILD_TYPES.contains(child.getOrganizationType())) {
            throw new InvalidOrganizationHierarchyException(
                    "A organização filha deve ser LIGA ou ASSOCIAÇÃO.");
        }
        if (child.getParentId() != null) {
            throw new OrganizationAssociationConflictException(clubId, child.getParentId());
        }

        child.setParentId(parentId);
        repository.save(child);

        return mapper.toDetailResponse(child);
    }

    @Transactional
    public void removeClubAssociation(UUID parentId, UUID clubId) {
        findEntityById(parentId);
        OrganizationEntity child = findEntityById(clubId);

        if (child.getParentId() == null || !child.getParentId().equals(parentId)) {
            throw new OrganizationAssociationNotFoundException(parentId, clubId);
        }

        child.setParentId(null);
        repository.save(child);
    }

    @Transactional
    /**
     * Valida o documento de uma organizacao: e obrigatorio informar CNPJ ou
     * CPF (um dos dois); o formato deve ser valido; o documento deve ser unico.
     */
    private void validateDocument(String document, DocumentType type, UUID currentId) {
        if (document == null || document.isBlank()) {
            return; // CNPJ opcional
        }
        if (type == null) {
            throw new InvalidDocumentException("Informe o tipo do documento (CNPJ ou CPF).");
        }
        if (!DocumentValidator.isValid(document, type)) {
            throw new InvalidDocumentException("Documento inválido: " + type.getCode());
        }
        String normalized = document.replaceAll("\\D", "");
        boolean duplicate = currentId == null
                ? repository.existsByDocument(normalized)
                : repository.existsByDocumentAndIdNot(normalized, currentId);
        if (duplicate) {
            throw new DuplicateDocumentException(normalized);
        }
    }

    /**
     * Valida o CPF do presidente: obrigatorio e com digitos validos.
     * O mesmo presidente pode presidir multiplas organizacoes (V27).
     */
    private void validatePresident(String presidentCpf) {
        if (presidentCpf == null || presidentCpf.isBlank()) {
            throw new InvalidDocumentException("Informe o CPF do presidente.");
        }
        if (!DocumentValidator.isValid(presidentCpf, DocumentType.CPF)) {
            throw new InvalidDocumentException("CPF do presidente inválido.");
        }
    }

    @Override
    public void assertExists(UUID id) {
        findEntityById(id);
    }

    @Override
    public String findTradeNameById(UUID id) {
        return findEntityById(id).getTradeName();
    }

    private OrganizationEntity findEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException(id));
    }

}
