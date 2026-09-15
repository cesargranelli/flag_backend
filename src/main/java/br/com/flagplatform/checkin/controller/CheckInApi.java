package br.com.flagplatform.checkin.controller;

import br.com.flagplatform.checkin.dto.request.CheckInStatusRequest;
import br.com.flagplatform.checkin.dto.request.MatchNumberRequest;
import br.com.flagplatform.checkin.dto.response.CheckInResponse;
import br.com.flagplatform.checkin.dto.response.ValidationResponse;
import br.com.flagplatform.common.security.SecurityExpressions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Check-in", description = "Endpoints para validar presença de atletas antes da partida")
public interface CheckInApi {

    @Operation(
            summary = "Listar check-in da partida",
            description = "Retorna o roster dos dois times do jogo com o status de check-in de cada atleta."
    )
    @GetMapping("/api/v1/games/{gameId}/checkin")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_COMMISSIONER)
    List<CheckInResponse> getCheckinList(
            @Parameter(description = "ID do jogo") @PathVariable UUID gameId);

    @Operation(
            summary = "Registrar check-in de atleta",
            description = "Marca um atleta do jogo como PRESENT ou NO_SHOW. "
                    + "Registra quem validou e quando."
    )
    @PostMapping("/api/v1/games/{gameId}/checkin/{athleteId}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_COMMISSIONER)
    CheckInResponse checkin(
            @Parameter(description = "ID do jogo") @PathVariable UUID gameId,
            @Parameter(description = "ID do atleta") @PathVariable UUID athleteId,
            @Valid @RequestBody CheckInStatusRequest request,
            @AuthenticationPrincipal UserDetails principal);

    @Operation(
            summary = "Validar atleta durante a partida",
            description = "Valida a entrada de um atleta durante a partida (IN_PROGRESS). "
                    + "Retorna NOT_REGISTERED se o atleta não estiver no roster dos times."
    )
    @PostMapping("/api/v1/games/{gameId}/checkin/{athleteId}/validate")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_COMMISSIONER)
    ValidationResponse validate(
            @Parameter(description = "ID do jogo") @PathVariable UUID gameId,
            @Parameter(description = "ID do atleta") @PathVariable UUID athleteId,
            @AuthenticationPrincipal UserDetails principal);

    @Operation(
            summary = "Definir numeração de partida do atleta",
            description = "Troca o número do atleta apenas para esta partida, sem alterar o "
                    + "número oficial. Body {number: null} limpa o override (volta ao oficial). "
                    + "Bloqueia número duplicado dentro do mesmo time na partida."
    )
    @PutMapping("/api/v1/games/{gameId}/checkin/{athleteId}/match-number")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_COMMISSIONER)
    CheckInResponse setMatchNumber(
            @Parameter(description = "ID do jogo") @PathVariable UUID gameId,
            @Parameter(description = "ID do atleta") @PathVariable UUID athleteId,
            @Valid @RequestBody MatchNumberRequest request);

    @Operation(
            summary = "Consultar validações do jogo",
            description = "Retorna o roster dos dois times com o status de validação de cada atleta. "
                    + "Acesso público."
    )
    @GetMapping("/api/v1/games/{gameId}/validations")
    List<CheckInResponse> getValidations(
            @Parameter(description = "ID do jogo") @PathVariable UUID gameId);
}
