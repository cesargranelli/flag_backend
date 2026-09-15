package br.com.flagplatform.institution.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.institution.dto.request.CreateInstitutionRequest;
import br.com.flagplatform.institution.dto.request.UpdateInstitutionRequest;
import br.com.flagplatform.institution.dto.response.InstitutionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Institutions", description = "Endpoints para gerenciar instituições (clubes/universidades)")
public interface InstitutionApi {

    @Operation(summary = "Listar instituições")
    @GetMapping("/api/v1/institutions")
    List<InstitutionResponse> list(@RequestParam(required = false) UUID organizationId);

    @Operation(summary = "Criar instituição")
    @PostMapping("/api/v1/institutions")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    InstitutionResponse create(@Valid @RequestBody CreateInstitutionRequest req);

    @Operation(summary = "Buscar instituição por id")
    @GetMapping("/api/v1/institutions/{id}")
    InstitutionResponse getById(@PathVariable UUID id);

    @Operation(summary = "Atualizar instituição")
    @PutMapping("/api/v1/institutions/{id}")
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    InstitutionResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateInstitutionRequest req);

    @Operation(summary = "Remover instituição")
    @DeleteMapping("/api/v1/institutions/{id}")
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id);

    @Operation(summary = "Definir organizações da instituição")
    @PutMapping("/api/v1/institutions/{id}/organizations")
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    InstitutionResponse setOrganizations(
            @PathVariable UUID id,
            @RequestBody com.fasterxml.jackson.databind.JsonNode node);
}
