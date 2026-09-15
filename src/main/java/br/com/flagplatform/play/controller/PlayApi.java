package br.com.flagplatform.play.controller;

import br.com.flagplatform.play.dto.request.CreatePlayRequest;
import br.com.flagplatform.play.dto.response.PlayResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static br.com.flagplatform.common.security.SecurityExpressions.ADMIN_OR_COMMISSIONER;

@Tag(name = "Plays", description = "Endpoints para play-by-play de jogos")
public interface PlayApi {

    @Operation(
            summary = "Listar lances de um jogo",
            description = "Retorna os lances (play-by-play) de um jogo, ordenados do mais recente ao mais antigo. Acesso público."
    )
    @GetMapping("/api/v1/games/{gameId}/plays")
    List<PlayResponse> findByGameId(
            @Parameter(description = "ID do jogo") @PathVariable UUID gameId);

    @Operation(
            summary = "Registrar lance",
            description = "Registra um novo lance (play) em um jogo em andamento. Requer autenticação."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não autenticado ou sem permissão")
    @PostMapping("/api/v1/games/{gameId}/plays")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(ADMIN_OR_COMMISSIONER)
    PlayResponse create(
            @Parameter(description = "ID do jogo") @PathVariable UUID gameId,
            @Valid @RequestBody CreatePlayRequest request);
}
