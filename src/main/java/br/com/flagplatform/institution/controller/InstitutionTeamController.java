package br.com.flagplatform.institution.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.team.controller.TeamController;
import br.com.flagplatform.team.dto.request.CreateTeamRequest;
import br.com.flagplatform.team.dto.response.TeamResponse;
import br.com.flagplatform.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Institution Teams", description = "Endpoints para gerenciamento de equipes de agremiações/instituições")
@RestController
@RequestMapping("/api/v1/institutions/{institutionId}/teams")
@RequiredArgsConstructor
public class InstitutionTeamController {

    private final TeamService teamService;

    @Operation(
            summary = "Criar time de instituição/agremiação",
            description = "Cria um time dentro de uma instituição (clube/universidade). Permitido para ADMIN, ORGANIZER ou MANAGER."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    public TeamResponse createForInstitution(
            @Parameter(description = "Id da instituição (clube/universidade)") @PathVariable UUID institutionId,
            @Valid @RequestBody CreateTeamRequest request,
            Authentication authentication) {
        return teamService.createForInstitution(institutionId, request, authentication.getName());
    }

    @Operation(
            summary = "Listar times de uma instituição/agremiação",
            description = "Lista os times de uma instituição (clube/universidade), ordenados por nome. Acesso público."
    )
    @GetMapping
    public List<TeamResponse> findByInstitutionId(
            @Parameter(description = "Id da instituição") @PathVariable UUID institutionId) {
        return teamService.findByInstitutionId(institutionId);
    }
}