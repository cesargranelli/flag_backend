package br.com.flagplatform.club.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClubRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 50)
        String shortName,

        @Size(max = 255)
        String sportName,

        @Size(max = 500)
        String logoUrl,

        @Size(max = 20)
        String document,

        @Size(max = 10)
        String documentType,

        @Size(max = 150)
        String presidentName,

        @Size(max = 14)
        String presidentCpf
) {
}
