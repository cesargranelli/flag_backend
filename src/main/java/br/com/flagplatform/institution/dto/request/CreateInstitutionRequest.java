package br.com.flagplatform.institution.dto.request;

import br.com.flagplatform.common.enums.InstitutionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateInstitutionRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull InstitutionType type,
        @Size(max = 6) List<@Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "color must be hex #RRGGBB") String> colors,
        List<UUID> organizationIds
) {}
