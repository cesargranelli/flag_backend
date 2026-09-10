package br.com.flagplatform.person.dto.request;

import br.com.flagplatform.common.enums.PersonRole;
import jakarta.validation.constraints.Size;

public record CreatePersonBatchItem(
        @Size(max = 150)
        String name,

        @Size(max = 14)
        String cpf,

        @Size(max = 500)
        String photoUrl,

        PersonRole role
) {
}
