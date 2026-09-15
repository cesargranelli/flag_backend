package br.com.flagplatform.user.controller;

import br.com.flagplatform.common.security.SecurityExpressions;
import br.com.flagplatform.user.dto.request.ChangeUserRoleRequest;
import br.com.flagplatform.user.dto.request.CreateUserRequest;
import br.com.flagplatform.user.dto.request.DevTokenRequest;
import br.com.flagplatform.user.dto.request.RegisterRequest;
import br.com.flagplatform.user.dto.response.DevTokenResponse;
import br.com.flagplatform.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Auth", description = "Cadastro e usuário atual")
public interface AuthApi {

    @Operation(
            summary = "Cadastrar usuário",
            description = "Cria um usuário com o papel ORGANIZER. Acesso público."
    )
    @PostMapping("/api/v1/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse register(@Valid @RequestBody RegisterRequest request);

    @Operation(
            summary = "Emitir token de desenvolvimento/teste",
            description = "Gera um token JWT com claims compatível com o parser de desenvolvimento do backend. Uso em dev/test."
    )
    @PostMapping("/api/v1/auth/dev-token")
    @ResponseStatus(HttpStatus.OK)
    DevTokenResponse generateDevToken(@Valid @RequestBody DevTokenRequest request);

    @Operation(
            summary = "Usuário atual",
            description = "Retorna o usuário autenticado no contexto do Spring Security (Firebase ID Token via header Authorization)."
    )
    @GetMapping("/api/v1/auth/me")
    UserResponse me(@AuthenticationPrincipal Object principal);

    @Operation(
            summary = "Criar usuário",
            description = "Cria um usuário com o papel informado (ADMIN, ORGANIZER, COMMISSIONER, REFEREE, MANAGER ou FAN). Exclusivo de ADMIN."
    )
    @PostMapping("/api/v1/auth/users")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(SecurityExpressions.ADMIN)
    UserResponse createUser(@Valid @RequestBody CreateUserRequest request);

    @Operation(
            summary = "Listar usuários",
            description = "Lista os usuários cadastrados, ordenados por nome. Exclusivo de ADMIN."
    )
    @GetMapping("/api/v1/auth/users")
    @PreAuthorize(SecurityExpressions.ADMIN)
    List<UserResponse> listUsers();

    @Operation(
            summary = "Listar usuários pendentes",
            description = "Lista os usuários aguardando aprovação. Exclusivo de ADMIN."
    )
    @GetMapping("/api/v1/auth/users/pending")
    @PreAuthorize(SecurityExpressions.ADMIN)
    List<UserResponse> listPending();

    @Operation(
            summary = "Aprovar usuário",
            description = "Ativa uma conta pendente de organizador. Exclusivo de ADMIN."
    )
    @PostMapping("/api/v1/auth/users/{id}/approve")
    @PreAuthorize(SecurityExpressions.ADMIN)
    UserResponse approve(@PathVariable UUID id);

    @Operation(
            summary = "Rejeitar usuário",
            description = "Rejeita uma conta pendente de organizador. Exclusivo de ADMIN."
    )
    @PostMapping("/api/v1/auth/users/{id}/reject")
    @PreAuthorize(SecurityExpressions.ADMIN)
    UserResponse reject(@PathVariable UUID id);

    @Operation(
            summary = "Alterar role de usuário",
            description = "Altera o papel (role) de um usuário. Exclusivo de ADMIN."
    )
    @PatchMapping("/api/v1/auth/users/{id}/role")
    @PreAuthorize(SecurityExpressions.ADMIN)
    UserResponse changeRole(@PathVariable UUID id, @Valid @RequestBody ChangeUserRoleRequest request);
}
