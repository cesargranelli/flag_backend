package br.com.flagplatform.game.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.game.dto.request.*;
import br.com.flagplatform.game.dto.response.*;
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

@Tag(name = "Games", description = "Endpoints para criar e gerenciar jogos")
public interface GameApi {

    @Operation(
            summary = "Listar jogos ao vivo",
            description = "Retorna jogos IN_PROGRESS e finalizados nas últimas 24h, com metadados da competição (modalidade, gênero). Acesso público."
    )
    @GetMapping("/api/v1/games/live")
    List<LiveGameResponse> findLiveGames();

    @Operation(
            summary = "Criar jogo",
            description = "Cria um novo jogo em uma rodada. Permitido apenas ao criador do campeonato ou ADMIN."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é o criador do campeonato nem ADMIN")
    @PostMapping("/api/v1/games")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    GameResponse create(@Valid @RequestBody CreateGameRequest request, Authentication authentication);

    @Operation(
            summary = "Importar jogos em lote",
            description = "Cria vários jogos de uma rodada de uma vez. Linhas inválidas/duplicadas não abortam as válidas. Permitido apenas ao criador do campeonato ou ADMIN."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é o criador do campeonato nem ADMIN")
    @PostMapping("/api/v1/rounds/{roundId}/games/batch")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    GameBatchResponse createBatch(
            @Parameter(description = "ID da rodada") @PathVariable UUID roundId,
            @Valid @RequestBody GameBatchRequest request,
            Authentication authentication);

    @Operation(
            summary = "Listar jogos por rodada",
            description = "Lista os jogos de uma rodada, ordenados por horário, com nomes de times e campo. Acesso público."
    )
    @GetMapping("/api/v1/rounds/{roundId}/games")
    List<GameSummaryResponse> findByRoundId(
            @Parameter(description = "ID da rodada") @PathVariable UUID roundId);

    @Operation(
            summary = "Listar jogos por competição",
            description = "Lista os jogos de uma competição (todas as categorias), ordenados por data, com nomes de times e campo. Acesso público."
    )
    @GetMapping("/api/v1/competitions/{competitionId}/games")
    List<GameSummaryResponse> findByCompetitionId(
            @Parameter(description = "ID da competição") @PathVariable UUID competitionId);

    @Operation(
            summary = "Buscar jogo por id",
            description = "Retorna o detalhe de um jogo. Acesso público."
    )
    @GetMapping("/api/v1/games/{id}")
    GameResponse findById(
            @Parameter(description = "ID do jogo") @PathVariable UUID id);

    @Operation(
            summary = "Atualizar jogo",
            description = "Atualiza horário, campo ou rodada de um jogo existente. Permitido apenas ao criador do campeonato ou ADMIN."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é o criador do campeonato nem ADMIN")
    @PutMapping("/api/v1/games/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    GameResponse update(
            @Parameter(description = "ID do jogo") @PathVariable UUID id,
            @Valid @RequestBody UpdateGameRequest request,
            Authentication authentication);

    @Operation(
            summary = "Atualizar status do jogo",
            description = "Atualiza o status de um jogo conforme as transições válidas (SCHEDULED->OPEN, OPEN->IN_PROGRESS, IN_PROGRESS->CONFERENCE, CONFERENCE->FINISHED, SCHEDULED/OPEN->CANCELLED). Requer autenticação."
    )
    @PatchMapping("/api/v1/games/{id}/status")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_COMMISSIONER)
    GameResponse updateStatus(
            @Parameter(description = "ID do jogo") @PathVariable UUID id,
            @Valid @RequestBody UpdateGameStatusRequest request);

    @Operation(
            summary = "Registrar resultado de partida",
            description = "Registra o placar final de um jogo em conferência (CONFERENCE), finaliza o jogo (FINISHED) e recalcula a classificação da categoria automaticamente. Requer autenticação."
    )
    @PostMapping("/api/v1/games/{id}/result")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_COMMISSIONER)
    GameResponse registerResult(
            @Parameter(description = "ID do jogo") @PathVariable UUID id,
            @Valid @RequestBody RegisterGameResultRequest request);

    @Operation(
            summary = "Adicionar ponto ao placar",
            description = "Adiciona 1 ponto ao time informado durante a partida (IN_PROGRESS) e registra o evento. Requer autenticação."
    )
    @PostMapping("/api/v1/games/{id}/score/events")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_COMMISSIONER)
    GameResponse addScoreEvent(
            @Parameter(description = "ID do jogo") @PathVariable UUID id,
            @Valid @RequestBody AddScoreEventRequest request);

    @Operation(
            summary = "Corrigir placar",
            description = "Define os pontos de casa e fora durante a partida (IN_PROGRESS). Requer autenticação."
    )
    @PatchMapping("/api/v1/games/{id}/score")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_COMMISSIONER)
    GameResponse correctScore(
            @Parameter(description = "ID do jogo") @PathVariable UUID id,
            @Valid @RequestBody UpdateScoreRequest request);

    @Operation(
            summary = "Histórico de pontuação",
            description = "Retorna os eventos de pontuação de um jogo, ordenados por data. Acesso público."
    )
    @GetMapping("/api/v1/games/{id}/score/events")
    List<ScoreEventResponse> listScoreEvents(
            @Parameter(description = "ID do jogo") @PathVariable UUID id);
}
