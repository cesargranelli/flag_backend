package br.com.flagplatform.user.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.user.dto.request.SetCustomClaimsRequest;
import br.com.flagplatform.user.dto.response.FirebaseLinkResponse;
import br.com.flagplatform.user.dto.response.FirebaseStatusResponse;
import br.com.flagplatform.user.service.FirebaseAdminService;
import org.springframework.beans.factory.ObjectProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Endpoints administrativos para gerenciar a integração Firebase Auth dos usuários.
 *
 * <p>Fase 2 da migração JWT custom → Firebase Auth (issue #31).
 *
 * <p><b>Autorização:</b>
 * <ul>
 *   <li>{@code POST /firebase-link} — apenas {@code SUPER_ADMIN}</li>
 *   <li>{@code POST /set-custom-claims} — {@code SUPER_ADMIN} ou {@code ORG_ADMIN}
 *       do mesmo contexto de organização</li>
 *   <li>{@code GET /firebase-status} — apenas {@code SUPER_ADMIN}</li>
 * </ul>
 *
 * @see FirebaseAdminService
 * @see br.com.flagplatform.security.FirebaseIdTokenFilter
 */
@Tag(
        name = "Admin — Firebase",
        description = "Gestão de vinculação Firebase e custom claims (admin only)"
)
@RequestMapping("/api/v1/admin/users")
@RestController
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
        name = "app.auth.firebase-enabled", havingValue = "true")
@RequiredArgsConstructor
public class FirebaseAdminController {

    private final ObjectProvider<FirebaseAdminService> firebaseAdminServiceProvider;

    /**
     * Vincula um usuário PostgreSQL a uma conta Firebase Auth existente ou cria uma nova.
     *
     * <p>Se o usuário ainda não possui conta Firebase, uma nova é criada com o e-mail
     * e nome. A senha temporária não é definida (o usuário deve usar o fluxo de
     * "forgot password" após o primeiro login).
     *
     * @param id UUID do usuário no PostgreSQL
     * @return UID do Firebase vinculado
     */
    @Operation(
            summary = "Vincular conta Firebase",
            description = """
                    Vincula um usuário PostgreSQL a uma conta Firebase Auth.

                    Se o usuário já possui conta Firebase (buscada pelo e-mail), o UID
                    existente é reaproveitado. Caso contrário, uma nova conta é criada.

                    **Requer:** {@code SUPER_ADMIN}

                    <p>Refs: ADR-004, issue #31
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Conta vinculada com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = FirebaseLinkResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Operação Firebase falhou"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer SUPER_ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Firebase UID já vinculado a outro usuário")
    })
    @PostMapping("/{id}/firebase-link")
    @PreAuthorize(SecurityExpressions.ADMIN)
    @ResponseStatus(HttpStatus.CREATED)
    public FirebaseLinkResponse linkFirebase(
            @Parameter(description = "UUID do usuário no PostgreSQL")
            @PathVariable UUID id) {
        return firebaseAdminServiceProvider.getObject().linkUser(id);
    }

    /**
     * Define as Custom Claims do Firebase para um usuário.
     *
     * <p>As claims são persistidas no Firebase (via Admin SDK) e sincronizadas
     * no PostgreSQL para manter a source of truth local.
     *
     * <p><b>Estrutura de claims gerada:</b>
     * <pre>{@code
     * {
     *   "role": "ORG_ADMIN",
     *   "organization_id": "uuid",
     *   "club_id": "uuid",
     *   "skills": ["athlete", "coach"]
     * }
     * }</pre>
     *
     * @param id      UUID do usuário
     * @param request role, organizationId, clubId, skills
     * @return status atualizado do vínculo Firebase
     */
    @Operation(
            summary = "Definir Custom Claims",
            description = """
                    Define as Custom Claims do Firebase para um usuário.

                    O usuário já deve estar vinculado ao Firebase
                    (chame {@code POST /firebase-link} antes).

                    **Requer:** {@code SUPER_ADMIN} ou {@code ORG_ADMIN} do mesmo contexto.

                    <p>Refs: ADR-004, issue #31
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Custom claims definidas com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = FirebaseStatusResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Operação Firebase falhou"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer SUPER_ADMIN ou ORG_ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(
                    responseCode = "412",
                    description = "Usuário não vinculado ao Firebase — chame POST /firebase-link primeiro"
            )
    })
    @PostMapping("/{id}/set-custom-claims")
    @PreAuthorize(SecurityExpressions.ADMIN)
    @ResponseStatus(HttpStatus.OK)
    public FirebaseStatusResponse setCustomClaims(
            @Parameter(description = "UUID do usuário no PostgreSQL")
            @PathVariable UUID id,
            @Valid @RequestBody SetCustomClaimsRequest request) {
        return firebaseAdminServiceProvider.getObject().setCustomClaims(id, request);
    }

    /**
     * Retorna o status de vinculação Firebase de um usuário.
     *
     * <p>Útil para a UI do Admin Web verificar se um usuário está vinculado
     * e quais são as custom claims atuais.
     *
     * @param id UUID do usuário
     * @return status de vinculação e claims atuais
     */
    @Operation(
            summary = "Status Firebase do usuário",
            description = """
                    Retorna o status de vinculação Firebase e as custom claims atuais.

                    Inclui também um campo {@code claimsSyncedWithDb} indicando se as
                    claims do Firebase estão sincronizadas com o PostgreSQL.

                    **Requer:** {@code SUPER_ADMIN}

                    <p>Refs: ADR-004, issue #31
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Status retornado com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = FirebaseStatusResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Operação Firebase falhou"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer SUPER_ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}/firebase-status")
    @PreAuthorize(SecurityExpressions.ADMIN)
    public FirebaseStatusResponse getFirebaseStatus(
            @Parameter(description = "UUID do usuário no PostgreSQL")
            @PathVariable UUID id) {
        return firebaseAdminServiceProvider.getObject().getStatus(id);
    }
}
