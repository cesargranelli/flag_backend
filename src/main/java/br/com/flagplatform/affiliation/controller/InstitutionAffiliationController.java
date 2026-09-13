package br.com.flagplatform.affiliation.controller;

import br.com.flagplatform.affiliation.dto.AffiliationResponse;
import br.com.flagplatform.affiliation.dto.RejectAffiliationRequest;
import br.com.flagplatform.affiliation.dto.RequestAffiliationRequest;
import br.com.flagplatform.affiliation.service.InstitutionAffiliationService;
import br.com.flagplatform.common.enums.AffiliationStatus;
import br.com.flagplatform.common.security.SecurityExpressions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Affiliations", description = "Endpoints de Filiação de Agremiações (Clubes e Universidades) a Organizações por Temporada")
@RestController
@RequiredArgsConstructor
public class InstitutionAffiliationController {

    private final InstitutionAffiliationService service;

    @Operation(summary = "Solicitar filiação da agremiação a uma organização")
    @PostMapping("/api/v1/institutions/{institutionId}/affiliations")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    public AffiliationResponse requestAffiliation(
            @PathVariable UUID institutionId,
            @Valid @RequestBody RequestAffiliationRequest req,
            Authentication authentication) {
        return service.requestAffiliation(institutionId, req, authentication.getName());
    }

    @Operation(summary = "Listar filiações de uma agremiação")
    @GetMapping("/api/v1/institutions/{institutionId}/affiliations")
    public List<AffiliationResponse> listByInstitution(@PathVariable UUID institutionId) {
        return service.listByInstitution(institutionId);
    }

    @Operation(summary = "Listar pedidos de filiação recebidos por uma organização")
    @GetMapping("/api/v1/organizations/{organizationId}/affiliations")
    public List<AffiliationResponse> listByOrganization(
            @PathVariable UUID organizationId,
            @RequestParam(required = false) String season,
            @RequestParam(required = false) AffiliationStatus status) {
        return service.listByOrganization(organizationId, season, status);
    }

    @Operation(summary = "Aprovar pedido de filiação de uma agremiação")
    @PostMapping("/api/v1/organizations/{organizationId}/affiliations/{affiliationId}/approve")
    @PreAuthorize(SecurityExpressions.ORGANIZATION_WRITE)
    public AffiliationResponse approve(
            @PathVariable UUID organizationId,
            @PathVariable UUID affiliationId,
            Authentication authentication) {
        return service.approve(organizationId, affiliationId, authentication.getName());
    }

    @Operation(summary = "Rejeitar pedido de filiação de uma agremiação")
    @PostMapping("/api/v1/organizations/{organizationId}/affiliations/{affiliationId}/reject")
    @PreAuthorize(SecurityExpressions.ORGANIZATION_WRITE)
    public AffiliationResponse reject(
            @PathVariable UUID organizationId,
            @PathVariable UUID affiliationId,
            @Valid @RequestBody RejectAffiliationRequest req,
            Authentication authentication) {
        return service.reject(organizationId, affiliationId, req, authentication.getName());
    }

    // -------------------------------------------------------------------------
    // Endpoints de Janelas / Períodos de Filiação
    // -------------------------------------------------------------------------

    @Operation(summary = "Abrir ou atualizar período de filiação de uma organização para uma temporada")
    @PostMapping("/api/v1/organizations/{organizationId}/affiliation-windows")
    @PreAuthorize(SecurityExpressions.ORGANIZATION_WRITE)
    public br.com.flagplatform.affiliation.dto.AffiliationWindowResponse openWindow(
            @PathVariable UUID organizationId,
            @Valid @RequestBody br.com.flagplatform.affiliation.dto.CreateAffiliationWindowRequest req,
            Authentication authentication) {
        return service.openWindow(organizationId, req, authentication.getName());
    }

    @Operation(summary = "Encerrar período de filiação de uma organização para uma temporada")
    @PostMapping("/api/v1/organizations/{organizationId}/affiliation-windows/{season}/close")
    @PreAuthorize(SecurityExpressions.ORGANIZATION_WRITE)
    public br.com.flagplatform.affiliation.dto.AffiliationWindowResponse closeWindow(
            @PathVariable UUID organizationId,
            @PathVariable String season) {
        return service.closeWindow(organizationId, season);
    }

    @Operation(summary = "Listar períodos de filiação cadastrados por uma organização")
    @GetMapping("/api/v1/organizations/{organizationId}/affiliation-windows")
    public List<br.com.flagplatform.affiliation.dto.AffiliationWindowResponse> listWindows(
            @PathVariable UUID organizationId) {
        return service.listWindows(organizationId);
    }

    @Operation(summary = "Listar organizações que estão com período de filiação ABERTO no momento")
    @GetMapping("/api/v1/affiliation-windows/open")
    public List<br.com.flagplatform.affiliation.dto.AffiliationWindowResponse> listOpenWindows() {
        return service.listOpenWindows();
    }
}

