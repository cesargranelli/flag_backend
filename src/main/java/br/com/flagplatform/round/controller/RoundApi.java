package br.com.flagplatform.round.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.round.dto.request.CreateRoundRequest;
import br.com.flagplatform.round.dto.request.UpdateRoundRequest;
import br.com.flagplatform.round.dto.response.RoundResponse;
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

@Tag(name = "Rounds", description = "Endpoints para criar e gerenciar rodadas")
public interface RoundApi {

    @Operation(
            summary = "Criar rodada",
            description = "Cria uma nova rodada em um campeonato. O competitionId é extraído do path. "
                    + "Permitido apenas ao criador do campeonato ou ADMIN, enquanto estiver em DRAFT."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é o criador do campeonato nem ADMIN")
    @ApiResponse(responseCode = "409", description = "Campeonato não está em status DRAFT")
    @PostMapping("/api/v1/competitions/{competitionId}/rounds")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    RoundResponse create(
            @Parameter(description = "ID do campeonato") @PathVariable UUID competitionId,
            @Valid @RequestBody CreateRoundRequest request,
            Authentication authentication);

    @Operation(
            summary = "Listar rodadas por campeonato",
            description = "Lista as rodadas de um campeonato, ordenadas por número. Acesso público."
    )
    @GetMapping("/api/v1/competitions/{competitionId}/rounds")
    List<RoundResponse> findByCompetitionId(
            @Parameter(description = "ID do campeonato") @PathVariable UUID competitionId);

    @Operation(
            summary = "Obter rodada",
            description = "Retorna o detalhe de uma rodada. Acesso público."
    )
    @GetMapping("/api/v1/rounds/{id}")
    RoundResponse findById(
            @Parameter(description = "ID da rodada") @PathVariable UUID id);

    @Operation(
            summary = "Atualizar rodada",
            description = "Atualiza uma rodada existente. O competitionId do path deve coincidir com o "
                    + "campeonato da rodada. Permitido apenas ao criador do campeonato ou ADMIN, "
                    + "enquanto estiver em DRAFT."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é o criador do campeonato nem ADMIN")
    @ApiResponse(responseCode = "409", description = "Campeonato não está em status DRAFT")
    @PutMapping("/api/v1/competitions/{competitionId}/rounds/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    RoundResponse update(
            @Parameter(description = "ID do campeonato") @PathVariable UUID competitionId,
            @Parameter(description = "ID da rodada") @PathVariable UUID id,
            @Valid @RequestBody UpdateRoundRequest request,
            Authentication authentication);
}
