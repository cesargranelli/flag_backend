package br.com.flagplatform.club.controller;

import br.com.flagplatform.club.dto.request.CreateClubRequest;
import br.com.flagplatform.club.dto.request.UpdateClubRequest;
import br.com.flagplatform.club.dto.response.ClubResponse;
import br.com.flagplatform.club.service.ClubService;
import br.com.flagplatform.common.security.SecurityExpressions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Clubs", description = "Endpoints para criar e gerenciar clubes esportivos")
@RestController
@RequiredArgsConstructor
public class ClubController {

    private final ClubService service;

    @Operation(
            summary = "Criar clube",
            description = "Cria um novo clube dentro de uma organização (federação/liga). Permitido apenas para ADMIN ou ORGANIZER."
    )
    @PostMapping("/api/v1/organizations/{organizationId}/clubs")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    public ClubResponse create(
            @Parameter(description = "Id da organização") @PathVariable UUID organizationId,
            @Valid @RequestBody CreateClubRequest request) {
        return service.create(organizationId, request);
    }

    @Operation(
            summary = "Listar clubes de uma organização",
            description = "Lista os clubes associados a uma organização com paginação e total no header X-Total-Count. Acesso público."
    )
    @GetMapping("/api/v1/organizations/{organizationId}/clubs")
    public List<ClubResponse> findByOrganization(
            @Parameter(description = "Id da organização") @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            HttpServletResponse response) {
        var result = service.findByOrganization(organizationId, page, size);
        response.setHeader("X-Total-Count", String.valueOf(result.total()));
        return result.items();
    }

    @Operation(
            summary = "Buscar clube por id",
            description = "Retorna os detalhes de um clube esportivo. Acesso público."
    )
    @GetMapping("/api/v1/clubs/{id}")
    public ClubResponse findById(
            @Parameter(description = "Id do clube") @PathVariable UUID id) {
        return service.findById(id);
    }

    @Operation(
            summary = "Atualizar clube",
            description = "Atualiza os dados de um clube existente. Permitido apenas para ADMIN ou ORGANIZER."
    )
    @PutMapping("/api/v1/clubs/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    public ClubResponse update(
            @Parameter(description = "Id do clube") @PathVariable UUID id,
            @Valid @RequestBody UpdateClubRequest request) {
        return service.update(id, request);
    }

}
