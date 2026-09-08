package br.com.flagplatform.affiliation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RequestAffiliationRequest(
        @NotNull UUID organizationId,
        @NotBlank String season
) {}
