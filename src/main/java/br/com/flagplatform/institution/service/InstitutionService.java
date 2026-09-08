package br.com.flagplatform.institution.service;

import br.com.flagplatform.institution.InstitutionLookup;
import br.com.flagplatform.institution.dto.request.CreateInstitutionRequest;
import br.com.flagplatform.institution.dto.request.UpdateInstitutionRequest;
import br.com.flagplatform.institution.dto.response.InstitutionResponse;
import br.com.flagplatform.institution.entity.InstitutionEntity;
import br.com.flagplatform.institution.mapper.InstitutionMapper;
import br.com.flagplatform.institution.repository.InstitutionOrganizationRepository;
import br.com.flagplatform.institution.repository.InstitutionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstitutionService implements InstitutionLookup {

    private final InstitutionRepository repository;
    private final InstitutionOrganizationRepository orgRepo;
    private final InstitutionMapper mapper;

    @Transactional
    public InstitutionResponse create(CreateInstitutionRequest req) {
        InstitutionEntity e = new InstitutionEntity();
        String tradeName = req.tradeName() != null && !req.tradeName().isBlank() ? req.tradeName() : req.name();
        String legalName = req.legalName() != null && !req.legalName().isBlank() ? req.legalName() : tradeName;
        e.setName(tradeName);
        e.setTradeName(tradeName);
        e.setLegalName(legalName);
        e.setType(req.type());
        e.setAbbreviation(req.abbreviation());
        e.setDocument(req.document());
        e.setDocumentType(req.documentType());
        e.setPresidentName(req.presidentName());
        e.setPresidentCpf(req.presidentCpf());
        e.setEmail(req.email());
        e.setPhone(req.phone());
        e.setWebsite(req.website());
        e.setInstagram(req.instagram());
        e.setCountry(req.country() != null && !req.country().isBlank() ? req.country() : "BR");
        e.setState(req.state());
        e.setCity(req.city());
        e.setLogoUrl(req.logoUrl());
        e.setPrimaryColor(req.primaryColor());
        e.setSecondaryColor(req.secondaryColor());
        e.setTertiaryColor(req.tertiaryColor());
        e.setQuaternaryColor(req.quaternaryColor());

        // Mantem array colors sincronizado com as 4 cores individuais se fornecidas
        List<String> colorsList = req.colors() != null ? new ArrayList<>(req.colors()) : new ArrayList<>();
        if (colorsList.isEmpty()) {
            if (req.primaryColor() != null) colorsList.add(req.primaryColor());
            if (req.secondaryColor() != null) colorsList.add(req.secondaryColor());
            if (req.tertiaryColor() != null) colorsList.add(req.tertiaryColor());
            if (req.quaternaryColor() != null) colorsList.add(req.quaternaryColor());
        }
        e.setColors(colorsList.isEmpty() ? null : colorsList.toArray(new String[0]));
        e.setStatus("ACTIVE");
        e = repository.save(e);
        if (req.organizationIds() != null && !req.organizationIds().isEmpty()) {
            orgRepo.setOrganizations(e.getId(), req.organizationIds());
        }
        return mapper.toResponse(e, orgRepo.findOrganizationIds(e.getId()));
    }

    @Transactional(readOnly = true)
    public List<InstitutionResponse> list() {
        return list(null);
    }

    @Transactional(readOnly = true)
    public List<InstitutionResponse> list(UUID organizationId) {
        if (organizationId == null) {
            return repository.findAll().stream()
                    .map(e -> mapper.toResponse(e, orgRepo.findOrganizationIds(e.getId())))
                    .toList();
        }
        List<UUID> instIds = orgRepo.findInstitutionIds(organizationId);
        if (instIds.isEmpty()) return List.of();
        return repository.findAllById(instIds).stream()
                .map(e -> mapper.toResponse(e, orgRepo.findOrganizationIds(e.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public InstitutionResponse getById(UUID id) {
        var e = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Institution not found"));
        return mapper.toResponse(e, orgRepo.findOrganizationIds(id));
    }

    @Transactional
    public InstitutionResponse update(UUID id, UpdateInstitutionRequest req) {
        var e = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Institution not found"));
        if (req.name() != null) e.setName(req.name());
        if (req.tradeName() != null) {
            e.setTradeName(req.tradeName());
            if (req.name() == null) e.setName(req.tradeName());
        }
        if (req.legalName() != null) e.setLegalName(req.legalName());
        if (req.type() != null) e.setType(req.type());
        if (req.abbreviation() != null) e.setAbbreviation(req.abbreviation());
        if (req.document() != null) e.setDocument(req.document());
        if (req.documentType() != null) e.setDocumentType(req.documentType());
        if (req.presidentName() != null) e.setPresidentName(req.presidentName());
        if (req.presidentCpf() != null) e.setPresidentCpf(req.presidentCpf());
        if (req.email() != null) e.setEmail(req.email());
        if (req.phone() != null) e.setPhone(req.phone());
        if (req.website() != null) e.setWebsite(req.website());
        if (req.instagram() != null) e.setInstagram(req.instagram());
        if (req.country() != null) e.setCountry(req.country());
        if (req.state() != null) e.setState(req.state());
        if (req.city() != null) e.setCity(req.city());
        if (req.logoUrl() != null) e.setLogoUrl(req.logoUrl());
        if (req.primaryColor() != null) e.setPrimaryColor(req.primaryColor());
        if (req.secondaryColor() != null) e.setSecondaryColor(req.secondaryColor());
        if (req.tertiaryColor() != null) e.setTertiaryColor(req.tertiaryColor());
        if (req.quaternaryColor() != null) e.setQuaternaryColor(req.quaternaryColor());

        if (req.colors() != null) {
            e.setColors(req.colors().toArray(new String[0]));
        } else if (req.primaryColor() != null || req.secondaryColor() != null) {
            List<String> colorsList = new ArrayList<>();
            if (e.getPrimaryColor() != null) colorsList.add(e.getPrimaryColor());
            if (e.getSecondaryColor() != null) colorsList.add(e.getSecondaryColor());
            if (e.getTertiaryColor() != null) colorsList.add(e.getTertiaryColor());
            if (e.getQuaternaryColor() != null) colorsList.add(e.getQuaternaryColor());
            if (!colorsList.isEmpty()) {
                e.setColors(colorsList.toArray(new String[0]));
            }
        }

        e = repository.save(e);
        if (req.organizationIds() != null) {
            orgRepo.setOrganizations(id, req.organizationIds());
        }
        return mapper.toResponse(e, orgRepo.findOrganizationIds(id));
    }

    @Transactional
    public void delete(UUID id) {
        var e = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Institution not found"));
        orgRepo.setOrganizations(id, List.of());
        repository.delete(e);
    }

    @Transactional
    public InstitutionResponse setOrganizations(UUID id, List<UUID> orgIds) {
        var e = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Institution not found"));
        orgRepo.setOrganizations(id, orgIds == null ? List.of() : orgIds);
        return mapper.toResponse(e, orgRepo.findOrganizationIds(id));
    }

    @Override
    public void assertExists(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Institution not found with id: " + id);
        }
    }

    @Override
    public String findTradeNameById(UUID id) {
        return repository.findById(id)
                .map(InstitutionEntity::getTradeName)
                .orElse(null);
    }
}
