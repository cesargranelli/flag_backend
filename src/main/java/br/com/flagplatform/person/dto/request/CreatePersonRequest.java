package br.com.flagplatform.person.dto.request;

import br.com.flagplatform.common.enums.Gender;
import br.com.flagplatform.common.enums.PersonRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePersonRequest(
        @NotBlank
        @Size(max = 150)
        String name,

        @NotBlank
        @Size(max = 14)
        String cpf,

        @Size(max = 500)
        String photoUrl,

        LocalDate birthDate,

        Gender gender,

        @NotNull
        PersonRole role
) {
}
