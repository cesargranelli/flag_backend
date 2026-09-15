package br.com.flagplatform.standing.controller;

import br.com.flagplatform.standing.dto.response.StandingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Tag(name = "Standings", description = "Endpoints para consultar a classificação")
public interface StandingApi {

    @Operation(
            summary = "Consultar classificação",
            description = "Retorna a tabela de classificação de um campeonato, ordenada por pontos, saldo de gols e gols pró. Acesso público."
    )
    @GetMapping("/api/v1/competitions/{competitionId}/standings")
    List<StandingResponse> findByCompetitionId(
            @Parameter(description = "ID do campeonato") @PathVariable UUID competitionId);
}
