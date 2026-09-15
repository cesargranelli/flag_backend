package br.com.flagplatform.team.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.team.dto.request.CreateTeamRequest;
import br.com.flagplatform.team.dto.request.EnrollTeamRequest;
import br.com.flagplatform.team.dto.request.UpdateCompetitionTeamRequest;
import br.com.flagplatform.team.dto.request.UpdateTeamRequest;
import br.com.flagplatform.team.dto.response.CompetitionTeamResponse;
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

@Tag(name = "Teams", description = "Endpoints para criar e gerenciar times")
public interface TeamApi {

    @Operation(
            summary = "Criar time",
            description = "Cria um time dentro de um clube (organização). Permitido apenas para ADMIN ou ORGANIZER."
    )
    @PostMapping("/api/v1/organizations/{organizationId}/teams")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    TeamResponse create(
            @Parameter(description = "ID da organização (clube)") @PathVariable UUID organizationId,
            @Valid @RequestBody CreateTeamRequest request,
            Authentication authentication);

    @Operation(
            summary = "Listar times de um clube",
            description = "Lista os times de uma organização (clube), ordenados por nome. Acesso público."
    )
    @GetMapping("/api/v1/organizations/{organizationId}/teams")
    List<TeamResponse> findByOrganizationId(
            @Parameter(description = "ID da organização") @PathVariable UUID organizationId);

    @Operation(
            summary = "Listar todos os times",
            description = "Lista todos os times da plataforma com o nome do clube vinculado. Acesso público."
    )
    @GetMapping("/api/v1/teams")
    List<TeamResponse> findAll();

    @Operation(
            summary = "Buscar time por id",
            description = "Retorna os detalhes de um time específico. Acesso público."
    )
    @GetMapping("/api/v1/teams/{id}")
    TeamResponse findById(
            @Parameter(description = "ID do time") @PathVariable UUID id);

    @Operation(
            summary = "Atualizar time",
            description = "Atualiza um time existente. Permitido para ADMIN, ORGANIZER ou MANAGER."
    )
    @PutMapping("/api/v1/teams/{id}")
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    TeamResponse update(
            @Parameter(description = "ID do time") @PathVariable UUID id,
            @Valid @RequestBody UpdateTeamRequest request,
            Authentication authentication);

    @Operation(
            summary = "Excluir time",
            description = "Remove um time. Permitido para ADMIN, ORGANIZER ou MANAGER."
    )
    @DeleteMapping("/api/v1/teams/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    void delete(
            @Parameter(description = "ID do time") @PathVariable UUID id,
            Authentication authentication);

    @Operation(
            summary = "Desativar time",
            description = "Exclusão lógica: marca o time como INACTIVE. Permitido para ADMIN, ORGANIZER ou MANAGER."
    )
    @PostMapping("/api/v1/teams/{id}/deactivate")
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    void deactivate(
            @Parameter(description = "ID do time") @PathVariable UUID id,
            Authentication authentication);

    @Operation(
            summary = "Reativar time",
            description = "Reverte a desativação lógica, voltando o time para ACTIVE. Permitido para ADMIN, ORGANIZER ou MANAGER."
    )
    @PostMapping("/api/v1/teams/{id}/reactivate")
    @PreAuthorize(SecurityExpressions.INSTITUTION_WRITE)
    void reactivate(
            @Parameter(description = "ID do time") @PathVariable UUID id,
            Authentication authentication);

    @Operation(
            summary = "Inscrever time em competição",
            description = "Inscreve um time em uma competição. Permitido para ADMIN, organizador da competição ou gestor da agremiação dona do time."
    )
    @PostMapping("/api/v1/competitions/{competitionId}/teams/{teamId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ACTIVE)
    CompetitionTeamResponse enrollInCompetition(
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId,
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            @RequestBody(required = false) EnrollTeamRequest request,
            Authentication authentication);

    @Operation(
            summary = "Atualizar alocação de time em competição",
            description = "Atualiza grupo, conferência, divisão ou seed de um time inscrito. Permitido apenas para ADMIN ou criador/organizador."
    )
    @PutMapping("/api/v1/competitions/{competitionId}/teams/{teamId}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    CompetitionTeamResponse updateAllocation(
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId,
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            @RequestBody UpdateCompetitionTeamRequest request,
            Authentication authentication);

    @Operation(
            summary = "Homologar/Aprovar time na competição",
            description = "Aprova a inscrição de um time. Permitido apenas para ADMIN ou criador/organizador."
    )
    @PostMapping("/api/v1/competitions/{competitionId}/teams/{teamId}/approve")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    CompetitionTeamResponse approveTeam(
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId,
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            Authentication authentication);

    @Operation(
            summary = "Rejeitar inscrição de time na competição",
            description = "Rejeita a inscrição de um time. Permitido apenas para ADMIN ou criador/organizador."
    )
    @PostMapping("/api/v1/competitions/{competitionId}/teams/{teamId}/reject")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    CompetitionTeamResponse rejectTeam(
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId,
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            Authentication authentication);

    @Operation(
            summary = "Listar times inscritos na competição",
            description = "Lista os times inscritos em uma competição. Acesso público."
    )
    @GetMapping("/api/v1/competitions/{competitionId}/teams")
    List<CompetitionTeamResponse> findByCompetitionId(
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId);

    @Operation(
            summary = "Listar competições de um time",
            description = "Lista todas as inscrições de um time em competições com seus respectivos status. Acesso público."
    )
    @GetMapping("/api/v1/teams/{teamId}/competitions")
    List<CompetitionTeamResponse> findCompetitionsByTeamId(
            @Parameter(description = "ID do time") @PathVariable UUID teamId);

    @Operation(
            summary = "Remover time da competição",
            description = "Remove a inscrição de um time em uma competição. Permitido apenas para ADMIN ou criador/organizador."
    )
    @DeleteMapping("/api/v1/competitions/{competitionId}/teams/{teamId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    void removeFromCompetition(
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId,
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            Authentication authentication);
}
