package br.com.flagplatform.club.controller;

import br.com.flagplatform.club.dto.request.CreateClubRequest;
import br.com.flagplatform.club.dto.request.UpdateClubRequest;
import br.com.flagplatform.club.dto.response.ClubResponse;
import br.com.flagplatform.common.security.SecurityExpressions;
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

@Tag(name = "Clubs", description = "Endpoints para criar e gerenciar clubes esportivos")
public interface ClubApi {

    @Operation(
            summary = "Criar clube",
            description = "Cria um novo clube dentro de uma organização (federação/liga). Permitido apenas para ADMIN ou ORGANIZER."
    )
    @PostMapping("/api/v1/organizations/{organizationId}/clubs")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    ClubResponse create(
            @Parameter(description = "ID da organização") @PathVariable UUID organizationId,
            @Valid @RequestBody CreateClubRequest request);

    @Operation(
            summary = "Listar clubes de uma organização",
            description = "Lista os clubes associados a uma organização com paginação e total no header X-Total-Count. Acesso público."
    )
    @GetMapping("/api/v1/organizations/{organizationId}/clubs")
    List<ClubResponse> findByOrganization(
            @Parameter(description = "ID da organização") @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            HttpServletResponse response);

    @Operation(
            summary = "Buscar clube por id",
            description = "Retorna os detalhes de um clube esportivo. Acesso público."
    )
    @GetMapping("/api/v1/clubs/{id}")
    ClubResponse findById(
            @Parameter(description = "ID do clube") @PathVariable UUID id);

    @Operation(
            summary = "Atualizar clube",
            description = "Atualiza os dados de um clube existente. Permitido apenas para ADMIN ou ORGANIZER."
    )
    @PutMapping("/api/v1/clubs/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    ClubResponse update(
            @Parameter(description = "ID do clube") @PathVariable UUID id,
            @Valid @RequestBody UpdateClubRequest request);
}
