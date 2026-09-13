package br.com.flagplatform.user.dto.request;

import br.com.flagplatform.common.enums.UserRole;
import jakarta.validation.constraints.NotNull;

/**
 * Request para alteração de role de um usuário.
 * Exclusivo de ADMIN.
 */
public record ChangeUserRoleRequest(
        @NotNull UserRole role
) {
}
