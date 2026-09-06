package br.com.flagplatform.institution.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.institution.dto.request.CreateInstitutionRequest;
import br.com.flagplatform.institution.dto.request.UpdateInstitutionRequest;
import br.com.flagplatform.institution.dto.response.InstitutionResponse;
import br.com.flagplatform.institution.service.InstitutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Institutions")
@RestController
@RequestMapping("/api/v1/institutions")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService service;

    @Operation(summary = "Listar instituições")
    @GetMapping
    public List<InstitutionResponse> list() { return service.list(); }

    @Operation(summary = "Criar instituição")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    public InstitutionResponse create(@Valid @RequestBody CreateInstitutionRequest req) { return service.create(req); }

    @Operation(summary = "Buscar instituição por id")
    @GetMapping("/{id}")
    public InstitutionResponse getById(@PathVariable UUID id) { return service.getById(id); }

    @Operation(summary = "Atualizar instituição")
    @PutMapping("/{id}")
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    public InstitutionResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateInstitutionRequest req) { return service.update(id, req); }

    @Operation(summary = "Remover instituição")
    @DeleteMapping("/{id}")
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }

    @Operation(summary = "Definir organizações da instituição")
    @PutMapping("/{id}/organizations")
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    public InstitutionResponse setOrganizations(@PathVariable UUID id, @RequestBody List<UUID> organizationIds) {
        return service.setOrganizations(id, organizationIds);
    }
}
