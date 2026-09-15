package br.com.flagplatform.institution.controller;

import br.com.flagplatform.institution.dto.request.CreateInstitutionRequest;
import br.com.flagplatform.institution.dto.request.UpdateInstitutionRequest;
import br.com.flagplatform.institution.dto.response.InstitutionResponse;
import br.com.flagplatform.institution.service.InstitutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InstitutionController implements InstitutionApi {

    private final InstitutionService service;

    @Override
    public List<InstitutionResponse> list(UUID organizationId) {
        return service.list(organizationId);
    }

    @Override
    public InstitutionResponse create(CreateInstitutionRequest req) {
        return service.create(req);
    }

    @Override
    public InstitutionResponse getById(UUID id) {
        return service.getById(id);
    }

    @Override
    public InstitutionResponse update(UUID id, UpdateInstitutionRequest req) {
        return service.update(id, req);
    }

    @Override
    public void delete(UUID id) {
        service.delete(id);
    }

    @Override
    public InstitutionResponse setOrganizations(UUID id, com.fasterxml.jackson.databind.JsonNode node) {
        List<UUID> organizationIds = new java.util.ArrayList<>();
        if (node != null) {
            if (node.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode item : node) {
                    organizationIds.add(UUID.fromString(item.asText()));
                }
            } else if (node.has("organizationIds") && node.get("organizationIds").isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode item : node.get("organizationIds")) {
                    organizationIds.add(UUID.fromString(item.asText()));
                }
            }
        }
        return service.setOrganizations(id, organizationIds);
    }
}
