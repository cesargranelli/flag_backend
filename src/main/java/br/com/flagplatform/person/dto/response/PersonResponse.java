package br.com.flagplatform.person.dto.response;

import br.com.flagplatform.common.enums.AthleteStatus;
import br.com.flagplatform.common.enums.Gender;
import br.com.flagplatform.common.enums.PersonRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PersonResponse(
        UUID id,
        String name,
        String cpf,
        String photoUrl,
        AthleteStatus status,
        LocalDate birthDate,
        Gender gender,
        PersonRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
