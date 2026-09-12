package br.com.flagplatform.person.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.person.dto.request.CreatePersonBatchRequest;
import br.com.flagplatform.person.dto.request.CreatePersonRequest;
import br.com.flagplatform.person.dto.request.UpdatePersonRequest;
import br.com.flagplatform.person.dto.response.PersonBatchResponse;
import br.com.flagplatform.person.dto.response.PersonResponse;
import br.com.flagplatform.person.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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

@Tag(name = "Persons", description = "Endpoints para criar e gerenciar pessoas")
@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonService service;

    @Operation(
            summary = "Criar pessoa",
            description = "Cria uma nova pessoa. Requer autenticação."
    )
    @PostMapping("/api/v1/persons")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    public PersonResponse create(@Valid @RequestBody CreatePersonRequest request) {
        return service.create(request);
    }

    @Operation(
            summary = "Validar carga em lote de pessoas (dry-run)",
            description = "Valida uma carga em lote sem gravar. Requer autenticação."
    )
    @PostMapping("/api/v1/persons/batch/dry-run")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    public PersonBatchResponse validateBatch(
            @Valid @RequestBody CreatePersonBatchRequest request) {
        return service.validateBatch(request);
    }

    @Operation(
            summary = "Importar carga em lote de pessoas",
            description = "Cria várias pessoas de uma vez. Linhas inválidas/duplicadas não abortam as válidas."
    )
    @PostMapping("/api/v1/persons/batch")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    public PersonBatchResponse createBatch(
            @Valid @RequestBody CreatePersonBatchRequest request) {
        return service.createBatch(request);
    }

    @Operation(
            summary = "Listar pessoas",
            description = "Lista todas as pessoas, ordenadas por nome. Acesso público."
    )
    @GetMapping("/api/v1/persons")
    public List<PersonResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            HttpServletResponse response) {
        var result = service.findAll(page, size);
        response.setHeader("X-Total-Count", String.valueOf(result.total()));
        return result.items();
    }

    @Operation(
            summary = "Buscar pessoa por id",
            description = "Retorna o detalhe de uma pessoa. Acesso público."
    )
    @GetMapping("/api/v1/persons/{id}")
    public PersonResponse findById(
            @Parameter(description = "Id da pessoa") @PathVariable UUID id) {
        return service.findById(id);
    }

    @Operation(
            summary = "Atualizar pessoa",
            description = "Atualiza uma pessoa existente. Requer autenticação."
    )
    @PutMapping("/api/v1/persons/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    public PersonResponse update(
            @Parameter(description = "Id da pessoa") @PathVariable UUID id,
            @Valid @RequestBody UpdatePersonRequest request) {
        return service.update(id, request);
    }

    @Operation(
            summary = "Desativar pessoa",
            description = "Exclusão lógica: marca a pessoa como INACTIVE. Requer ADMIN ou ORGANIZER."
    )
    @DeleteMapping("/api/v1/persons/{id}")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    public void deactivate(
            @Parameter(description = "Id da pessoa") @PathVariable UUID id) {
        service.deactivate(id);
    }

    @Operation(
            summary = "Reativar pessoa",
            description = "Reverte a desativação lógica, voltando a pessoa para ACTIVE. Requer ADMIN ou ORGANIZER."
    )
    @PostMapping("/api/v1/persons/{id}/reactivate")
    @PreAuthorize(SecurityExpressions.ADMIN_OR_ORGANIZER)
    public void reactivate(
            @Parameter(description = "Id da pessoa") @PathVariable UUID id) {
        service.reactivate(id);
    }

}
