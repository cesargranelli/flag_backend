package br.com.flagplatform.user.dto.request;

import br.com.flagplatform.common.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateUserRequest(
        @NotBlank
        @Size(min = 2, max = 120)
        String name,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 6, max = 72)
        String password,

        @NotNull
        UserRole role,

        /**
         * UID do Firebase Auth. Preenchido quando o usuário é criado via Firebase
         * (fluxo de migração) ou vinculado ao Firebase depois.
         */
        String firebaseUid,

        /**
         * Skills do usuário no contexto do Flag Football (athlete, coach, referee, manager).
         * Pode ser preenchido depois via custom claims do Firebase.
         */
        List<String> skills
) {
}
