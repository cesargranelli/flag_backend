package br.com.flagplatform.institution.dto.request;

import br.com.flagplatform.common.enums.InstitutionType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record UpdateInstitutionRequest(
        @Size(max = 150) String name,
        InstitutionType type,
        @Size(max = 6) List<@Pattern(regexp = "^#[0-9A-Fa-f]{6}$") String> colors,
        List<UUID> organizationIds
) {}
