package br.com.flagplatform.institution.service;

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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstitutionService {

    private final InstitutionRepository repository;
    private final InstitutionOrganizationRepository orgRepo;
    private final InstitutionMapper mapper;

    @Transactional
    public InstitutionResponse create(CreateInstitutionRequest req) {
        InstitutionEntity e = new InstitutionEntity();
        e.setName(req.name());
        e.setType(req.type());
        e.setColors(req.colors() == null ? null : req.colors().toArray(new String[0]));
        e.setStatus("ACTIVE");
        e = repository.save(e);
        if (req.organizationIds() != null && !req.organizationIds().isEmpty()) {
            orgRepo.setOrganizations(e.getId(), req.organizationIds());
        }
        return mapper.toResponse(e, orgRepo.findOrganizationIds(e.getId()));
    }

    @Transactional(readOnly = true)
    public List<InstitutionResponse> list() {
        return repository.findAll().stream()
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
        if (req.type() != null) e.setType(req.type());
        if (req.colors() != null) e.setColors(req.colors().toArray(new String[0]));
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
}
