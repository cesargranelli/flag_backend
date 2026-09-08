package br.com.flagplatform.affiliation.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectAffiliationRequest(
        @NotBlank String reason
) {}
