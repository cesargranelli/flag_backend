package br.com.flagplatform.organization.dto.request;

import br.com.flagplatform.common.enums.DocumentType;
import br.com.flagplatform.common.enums.OrganizationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateOrganizationRequest(
        @Size(max = 150)
        String legalName,

        @Size(max = 100)
        String tradeName,

        @Size(max = 20)
        String abbreviation,

        OrganizationType organizationType,

        @Size(max = 20)
        String document,

        DocumentType documentType,

        @Size(max = 150)
        String presidentName,

        @Size(max = 14)
        String presidentCpf,

        @Email
        @Size(max = 150)
        String email,

        @Size(max = 30)
        String phone,

        @Size(max = 255)
        String website,

        @Size(max = 100)
        String instagram,

        @Size(min = 2, max = 2)
        String country,

        @Size(max = 100)
        String state,

        @Size(max = 100)
        String city,

        @Size(max = 500)
        String logoUrl,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "primaryColor must be hex #RRGGBB")
        @Size(max = 7)
        String primaryColor,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "secondaryColor must be hex #RRGGBB")
        @Size(max = 7)
        String secondaryColor,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "tertiaryColor must be hex #RRGGBB")
        @Size(max = 7)
        String tertiaryColor,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "quaternaryColor must be hex #RRGGBB")
        @Size(max = 7)
        String quaternaryColor,

        String timezone,

        String locale
) {
}
