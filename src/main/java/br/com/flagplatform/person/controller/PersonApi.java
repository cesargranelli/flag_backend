package br.com.flagplatform.person.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.person.dto.request.CreatePersonBatchRequest;
import br.com.flagplatform.person.dto.request.CreatePersonRequest;
import br.com.flagplatform.person.dto.request.UpdatePersonRequest;
import br.com.flagplatform.person.dto.response.PersonBatchResponse;
import br.com.flagplatform.person.dto.response.PersonResponse;
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

@Tag(name = "Persons", description = "Endpoints para criar e gerenciar pessoas")
public interface PersonApi {

    @Operation(
            summary = "Criar pessoa",
            description = "Cria uma nova pessoa. Requer autenticação."
    )
    @PostMapping("/api/v1/persons")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    PersonResponse create(@Valid @RequestBody CreatePersonRequest request);

    @Operation(
            summary = "Validar carga em lote de pessoas (dry-run)",
            description = "Valida uma carga em lote sem gravar. Requer autenticação."
    )
    @PostMapping("/api/v1/persons/batch/dry-run")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    PersonBatchResponse validateBatch(@Valid @RequestBody CreatePersonBatchRequest request);

    @Operation(
            summary = "Importar carga em lote de pessoas",
            description = "Cria várias pessoas de uma vez. Linhas inválidas/duplicadas não abortam as válidas."
    )
    @PostMapping("/api/v1/persons/batch")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    PersonBatchResponse createBatch(@Valid @RequestBody CreatePersonBatchRequest request);

    @Operation(
            summary = "Listar pessoas",
            description = "Lista todas as pessoas, ordenadas por nome. Acesso público."
    )
    @GetMapping("/api/v1/persons")
    List<PersonResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            HttpServletResponse response);

    @Operation(
            summary = "Buscar pessoa por id",
            description = "Retorna o detalhe de uma pessoa. Acesso público."
    )
    @GetMapping("/api/v1/persons/{id}")
    PersonResponse findById(
            @Parameter(description = "ID da pessoa") @PathVariable UUID id);

    @Operation(
            summary = "Atualizar pessoa",
            description = "Atualiza uma pessoa existente. Requer autenticação."
    )
    @PutMapping("/api/v1/persons/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    PersonResponse update(
            @Parameter(description = "ID da pessoa") @PathVariable UUID id,
            @Valid @RequestBody UpdatePersonRequest request);

    @Operation(
            summary = "Desativar pessoa",
            description = "Exclusão lógica: marca a pessoa como INACTIVE. Requer ADMIN ou ORGANIZER."
    )
    @DeleteMapping("/api/v1/persons/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    void deactivate(
            @Parameter(description = "ID da pessoa") @PathVariable UUID id);

    @Operation(
            summary = "Reativar pessoa",
            description = "Reverte a desativação lógica, voltando a pessoa para ACTIVE. Requer ADMIN ou ORGANIZER."
    )
    @PostMapping("/api/v1/persons/{id}/reactivate")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    void reactivate(
            @Parameter(description = "ID da pessoa") @PathVariable UUID id);
}
