package br.com.flagplatform.roster.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.roster.dto.request.AddRosterEntryRequest;
import br.com.flagplatform.roster.dto.request.RosterBatchRequest;
import br.com.flagplatform.roster.dto.response.RosterBatchResponse;
import br.com.flagplatform.roster.dto.response.RosterEntryResponse;
import br.com.flagplatform.roster.dto.response.RosterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Roster", description = "Endpoints para gerenciar o elenco dos times")
public interface RosterApi {

    @Operation(
            summary = "Inscrever atleta no time",
            description = "Adiciona um atleta ao elenco de um time em uma competição. Permitido apenas para ADMIN ou ORGANIZER."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é ADMIN nem ORGANIZER")
    @PostMapping("/api/v1/teams/{teamId}/competitions/{competitionId}/roster")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    RosterEntryResponse add(
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId,
            @Valid @RequestBody AddRosterEntryRequest request,
            Authentication authentication);

    @Operation(
            summary = "Importar elenco em lote",
            description = "Inscreve vários atletas em um time de uma vez para uma competição. Atletas já inscritos são pulados. Permitido apenas para ADMIN ou ORGANIZER."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é ADMIN nem ORGANIZER")
    @PostMapping("/api/v1/teams/{teamId}/competitions/{competitionId}/roster/batch")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    RosterBatchResponse createBatch(
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId,
            @Valid @RequestBody RosterBatchRequest request,
            Authentication authentication);

    @Operation(
            summary = "Listar elencos do time",
            description = "Lista todos os elencos (rosters) de um time, independente da competição, ordenados por criação (mais recentes primeiro). Acesso público."
    )
    @GetMapping("/api/v1/teams/{teamId}/rosters")
    List<RosterResponse> findRostersByTeam(
            @Parameter(description = "ID do time") @PathVariable UUID teamId);

    @Operation(
            summary = "Listar elenco do time na competição",
            description = "Lista os atletas inscritos em um time para uma competição, ordenados por nome. Acesso público."
    )
    @GetMapping("/api/v1/teams/{teamId}/competitions/{competitionId}/roster")
    List<RosterEntryResponse> findRosterByTeamAndCompetition(
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId);

    @Operation(
            summary = "Remover atleta do time",
            description = "Remove um atleta do elenco de um time em uma competição. Permitido apenas para ADMIN ou ORGANIZER."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é ADMIN nem ORGANIZER")
    @DeleteMapping("/api/v1/teams/{teamId}/competitions/{competitionId}/roster/{athleteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    void remove(
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId,
            @Parameter(description = "ID do atleta") @PathVariable UUID athleteId,
            Authentication authentication);

    @Operation(
            summary = "Desativar elenco do time na competição",
            description = "Marca o elenco (roster) de um time em uma competição como INACTIVE. "
                    + "Permitido apenas para ADMIN ou ORGANIZER."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é ADMIN nem ORGANIZER")
    @PostMapping("/api/v1/teams/{teamId}/competitions/{competitionId}/roster/deactivate")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    void deactivate(
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId,
            Authentication authentication);

    @Operation(
            summary = "Reativar elenco do time na competição",
            description = "Marca o elenco (roster) de um time em uma competição como ACTIVE. "
                    + "Permitido apenas para ADMIN ou ORGANIZER."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é ADMIN nem ORGANIZER")
    @PostMapping("/api/v1/teams/{teamId}/competitions/{competitionId}/roster/reactivate")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    void reactivate(
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId,
            Authentication authentication);

    @Operation(
            summary = "Listar elenco base do time",
            description = "Lista os atletas do elenco-base de um time (sem competição vinculada), ordenados por número e nome. Acesso público."
    )
    @GetMapping("/api/v1/teams/{teamId}/roster")
    List<RosterEntryResponse> findBaseRoster(
            @Parameter(description = "ID do time") @PathVariable UUID teamId);

    @Operation(
            summary = "Adicionar atleta ao elenco base",
            description = "Adiciona um atleta ao elenco-base do time (sem competição). Cria o elenco-base se ainda não existir. Permitido apenas para ADMIN ou ORGANIZER."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é ADMIN nem ORGANIZER")
    @PostMapping("/api/v1/teams/{teamId}/roster")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    RosterEntryResponse addToBaseRoster(
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            @Valid @RequestBody AddRosterEntryRequest request,
            Authentication authentication);

    @Operation(
            summary = "Remover atleta do elenco base",
            description = "Remove um atleta do elenco-base do time. Permitido apenas para ADMIN ou ORGANIZER."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é ADMIN nem ORGANIZER")
    @DeleteMapping("/api/v1/teams/{teamId}/roster/{athleteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    void removeFromBaseRoster(
            @Parameter(description = "ID do time") @PathVariable UUID teamId,
            @Parameter(description = "ID do atleta") @PathVariable UUID athleteId,
            Authentication authentication);
}
