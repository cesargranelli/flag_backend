package br.com.flagplatform.organization.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.organization.dto.request.AssociateClubRequest;
import br.com.flagplatform.organization.dto.request.CreateOrganizationRequest;
import br.com.flagplatform.organization.dto.request.UpdateOrganizationRequest;
import br.com.flagplatform.organization.dto.response.OrganizationCreatedResponse;
import br.com.flagplatform.organization.dto.response.OrganizationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Organizations", description = "Endpoints para criar e gerenciar organizações esportivas")
public interface OrganizationApi {

    @Operation(
            summary = "Criar organização",
            description = "Cria uma nova organização esportiva. Requer autenticação."
    )
    @PostMapping("/api/v1/organizations")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    OrganizationCreatedResponse create(@Valid @RequestBody CreateOrganizationRequest request);

    @Operation(
            summary = "Listar organizações",
            description = "Lista as organizações esportivas ativas, com paginação (page/size) e total no header X-Total-Count. "
                    + "Com includeDisabled=true e role ADMIN, inclui também as desativadas. Acesso público."
    )
    @GetMapping("/api/v1/organizations")
    List<OrganizationResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "false") boolean includeDisabled,
            Authentication authentication,
            HttpServletResponse response);

    @Operation(
            summary = "Buscar organização por id",
            description = "Retorna o detalhe de uma organização esportiva. Organizações desativadas são visíveis apenas ao ADMIN. Acesso público."
    )
    @GetMapping("/api/v1/organizations/{id}")
    OrganizationResponse getById(
            @Parameter(description = "ID da organização") @PathVariable UUID id,
            Authentication authentication);

    @Operation(
            summary = "Atualizar organização",
            description = "Atualiza os dados cadastrais da organização esportiva. Requer ADMIN ou ORGANIZER."
    )
    @PutMapping("/api/v1/organizations/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    OrganizationResponse update(
            @Parameter(description = "ID da organização") @PathVariable UUID id,
            @Valid @RequestBody UpdateOrganizationRequest request);

    @Operation(
            summary = "Desativar organização",
            description = "Exclusão lógica: marca a organização como INACTIVE. Ela deixa de aparecer nas listagens e só o ADMIN pode reativar."
    )
    @DeleteMapping("/api/v1/organizations/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    void deactivate(
            @Parameter(description = "ID da organização") @PathVariable UUID id);

    @Operation(
            summary = "Reativar organização",
            description = "Reverte a desativação lógica, voltando a organização para ACTIVE. Exclusivo do ADMIN."
    )
    @PostMapping("/api/v1/organizations/{id}/reactivate")
    @PreAuthorize(SecurityExpressions.ADMIN)
    void reactivate(
            @Parameter(description = "ID da organização") @PathVariable UUID id);

    @Operation(
            summary = "Listar clubes/universidades de uma organização",
            description = "Lista as organizações filhas (CLUB/UNIVERSITY) associadas à organização informada, "
                    + "ordenadas por razão social. Acesso público."
    )
    @GetMapping("/api/v1/organizations/{id}/clubs")
    List<OrganizationResponse> listClubs(
            @Parameter(description = "ID da organização (federação/liga/associação)") @PathVariable UUID id);

    @Operation(
            summary = "Associar clube/universidade a uma organização",
            description = "Associa uma organização filha (CLUB/UNIVERSITY) à organização pai "
                    + "(FEDERATION/LEAGUE/ASSOCIATION). Requer ADMIN ou ORGANIZER. "
                    + "Retorna 409 se a organização filha já estiver associada."
    )
    @PostMapping("/api/v1/organizations/{id}/clubs")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    OrganizationResponse associateClub(
            @Parameter(description = "ID da organização pai") @PathVariable UUID id,
            @Valid @RequestBody AssociateClubRequest request);

    @Operation(
            summary = "Remover associação de clube/universidade",
            description = "Remove a associação de uma organização filha à organização pai "
                    + "(parent_id volta a null). Requer ADMIN ou ORGANIZER."
    )
    @DeleteMapping("/api/v1/organizations/{id}/clubs/{clubId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    void removeClub(
            @Parameter(description = "ID da organização pai") @PathVariable UUID id,
            @Parameter(description = "ID da organização filha (clube/universidade)") @PathVariable UUID clubId);
}
