package br.com.flagplatform.division.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.division.dto.request.CreateDivisionRequest;
import br.com.flagplatform.division.dto.request.UpdateDivisionRequest;
import br.com.flagplatform.division.dto.response.DivisionResponse;
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

@Tag(name = "Divisions", description = "Endpoints para criar e gerenciar divisões")
public interface DivisionApi {

    @Operation(
            summary = "Criar divisão",
            description = "Cria uma divisão dentro de um campeonato, opcionalmente "
                    + "vinculada a uma conferência. Permitido apenas ao criador do campeonato ou ADMIN."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é o criador do campeonato nem ADMIN")
    @PostMapping("/api/v1/competitions/{competitionId}/divisions")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    DivisionResponse create(
            @Parameter(description = "ID do campeonato") @PathVariable UUID competitionId,
            @Valid @RequestBody CreateDivisionRequest request,
            Authentication authentication);

    @Operation(
            summary = "Listar divisões por campeonato",
            description = "Lista as divisões de um campeonato, ordenadas por nome. Acesso público."
    )
    @GetMapping("/api/v1/competitions/{competitionId}/divisions")
    List<DivisionResponse> findByCompetitionId(
            @Parameter(description = "ID do campeonato") @PathVariable UUID competitionId);

    @Operation(
            summary = "Buscar divisão por id",
            description = "Retorna o detalhe de uma divisão. Acesso público."
    )
    @GetMapping("/api/v1/divisions/{id}")
    DivisionResponse findById(
            @Parameter(description = "ID da divisão") @PathVariable UUID id);

    @Operation(
            summary = "Atualizar divisão",
            description = "Atualiza uma divisão existente. Permitido apenas ao criador do campeonato ou ADMIN."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é o criador do campeonato nem ADMIN")
    @PutMapping("/api/v1/divisions/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    DivisionResponse update(
            @Parameter(description = "ID da divisão") @PathVariable UUID id,
            @Valid @RequestBody UpdateDivisionRequest request,
            Authentication authentication);

    @Operation(
            summary = "Excluir divisão",
            description = "Remove uma divisão. Permitido apenas ao criador do campeonato ou ADMIN, "
                    + "enquanto estiver em status DRAFT."
    )
    @ApiResponse(responseCode = "403", description = "Usuário não é o criador do campeonato nem ADMIN")
    @DeleteMapping("/api/v1/divisions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    void delete(
            @Parameter(description = "ID da divisão") @PathVariable UUID id,
            Authentication authentication);
}
