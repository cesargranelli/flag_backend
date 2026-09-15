package br.com.flagplatform.venue.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.venue.dto.request.CreateVenueRequest;
import br.com.flagplatform.venue.dto.request.UpdateVenueRequest;
import br.com.flagplatform.venue.dto.response.VenueResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Venues", description = "Endpoints para criar e gerenciar campos de jogo")
public interface VenueApi {

    @Operation(
            summary = "Criar campo de jogo",
            description = "Cria um novo campo de jogo. Requer autenticação."
    )
    @PostMapping("/api/v1/venues")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    VenueResponse create(@Valid @RequestBody CreateVenueRequest request);

    @Operation(
            summary = "Listar campos de jogo",
            description = "Lista todos os campos de jogo, ordenados por nome. Acesso público."
    )
    @GetMapping("/api/v1/venues")
    List<VenueResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            HttpServletResponse response);

    @Operation(
            summary = "Buscar campo de jogo por id",
            description = "Retorna o detalhe de um campo de jogo. Acesso público."
    )
    @GetMapping("/api/v1/venues/{id}")
    VenueResponse findById(
            @Parameter(description = "ID do campo de jogo") @PathVariable UUID id);

    @Operation(
            summary = "Atualizar campo de jogo",
            description = "Atualiza um campo de jogo existente. Requer autenticação."
    )
    @PutMapping("/api/v1/venues/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    VenueResponse update(
            @Parameter(description = "ID do campo de jogo") @PathVariable UUID id,
            @Valid @RequestBody UpdateVenueRequest request);
}
