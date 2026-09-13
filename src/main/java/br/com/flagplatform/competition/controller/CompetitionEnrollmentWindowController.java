package br.com.flagplatform.competition.controller;

import br.com.flagplatform.competition.dto.request.CreateCompetitionEnrollmentWindowRequest;
import br.com.flagplatform.competition.dto.response.CompetitionEnrollmentWindowResponse;
import br.com.flagplatform.competition.service.CompetitionEnrollmentWindowService;
import br.com.flagplatform.common.security.SecurityExpressions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Competition Enrollment Windows", description = "Endpoints para gerenciar janelas de inscrição de equipes em competições")
@RestController
@RequiredArgsConstructor
public class CompetitionEnrollmentWindowController {

    private final CompetitionEnrollmentWindowService service;

    @Operation(
            summary = "Abrir janela de inscrição",
            description = "Abre ou atualiza a janela de inscrição de equipes para uma competição. "
                    + "Permitido apenas ao criador da competição ou ADMIN."
    )
    @PostMapping("/api/v1/competitions/{competitionId}/enrollment-windows")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    public CompetitionEnrollmentWindowResponse openWindow(
            @Parameter(description = "Id da competição") @PathVariable UUID competitionId,
            @Valid @RequestBody CreateCompetitionEnrollmentWindowRequest request,
            Authentication authentication) {
        return service.openWindow(competitionId, request, authentication.getName());
    }

    @Operation(
            summary = "Encerrar janela de inscrição",
            description = "Encerra a janela de inscrição de equipes de uma competição. "
                    + "Permitido apenas ao criador da competição ou ADMIN."
    )
    @PostMapping("/api/v1/competitions/{competitionId}/enrollment-windows/close")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    public CompetitionEnrollmentWindowResponse closeWindow(
            @Parameter(description = "Id da competição") @PathVariable UUID competitionId) {
        return service.closeWindow(competitionId);
    }

    @Operation(
            summary = "Buscar janela de inscrição da competição",
            description = "Retorna a janela de inscrição de equipes de uma competição. Acesso público."
    )
    @GetMapping("/api/v1/competitions/{competitionId}/enrollment-windows")
    public CompetitionEnrollmentWindowResponse findByCompetitionId(
            @Parameter(description = "Id da competição") @PathVariable UUID competitionId) {
        return service.findByCompetitionId(competitionId);
    }

    @Operation(
            summary = "Listar competições com janela de inscrição aberta",
            description = "Retorna todas as competições que possuem janela de inscrição aberta no momento. "
                    + "Usado pela agremiação para listar competições disponíveis para inscrição."
    )
    @GetMapping("/api/v1/enrollment-windows/open")
    public List<CompetitionEnrollmentWindowResponse> listOpenWindows() {
        return service.listOpenWindows();
    }
}
