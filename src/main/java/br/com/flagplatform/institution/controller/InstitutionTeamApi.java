package br.com.flagplatform.institution.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.team.dto.request.CreateTeamRequest;
import br.com.flagplatform.team.dto.response.TeamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Institution Teams", description = "Endpoints para gerenciamento de equipes de agremiações/instituições")
public interface InstitutionTeamApi {

    @Operation(
            summary = "Criar time de instituição/agremiação",
            description = "Cria um time dentro de uma instituição (clube/universidade). Permitido para ADMIN, ORGANIZER ou MANAGER."
    )
    @PostMapping("/api/v1/institutions/{institutionId}/teams")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    TeamResponse createForInstitution(
            @Parameter(description = "ID da instituição (clube/universidade)") @PathVariable UUID institutionId,
            @Valid @RequestBody CreateTeamRequest request,
            Authentication authentication);

    @Operation(
            summary = "Listar times de uma instituição/agremiação",
            description = "Lista os times de uma instituição (clube/universidade), ordenados por nome. Acesso público."
    )
    @GetMapping("/api/v1/institutions/{institutionId}/teams")
    List<TeamResponse> findByInstitutionId(
            @Parameter(description = "ID da instituição") @PathVariable UUID institutionId);
}
