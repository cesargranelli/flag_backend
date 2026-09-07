package br.com.flagplatform.institution.dto.request;

import br.com.flagplatform.common.enums.DocumentType;
import br.com.flagplatform.common.enums.InstitutionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record UpdateInstitutionRequest(
        @Size(max = 150) String name,
        @Size(max = 255) String legalName,
        @Size(max = 255) String tradeName,
        InstitutionType type,
        @Size(max = 20) String abbreviation,
        @Size(max = 20) String document,
        DocumentType documentType,
        @Size(max = 150) String presidentName,
        @Size(max = 14) String presidentCpf,
        @Email @Size(max = 150) String email,
        @Size(max = 30) String phone,
        @Size(max = 255) String website,
        @Size(max = 100) String instagram,
        @Size(min = 2, max = 2) String country,
        @Size(max = 100) String state,
        @Size(max = 100) String city,
        @Size(max = 500) String logoUrl,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "primaryColor must be hex #RRGGBB") @Size(max = 7) String primaryColor,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "secondaryColor must be hex #RRGGBB") @Size(max = 7) String secondaryColor,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "tertiaryColor must be hex #RRGGBB") @Size(max = 7) String tertiaryColor,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "quaternaryColor must be hex #RRGGBB") @Size(max = 7) String quaternaryColor,
        @Size(max = 6) List<@Pattern(regexp = "^#[0-9A-Fa-f]{6}$") String> colors,
        List<UUID> organizationIds
) {}
