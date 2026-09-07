package br.com.flagplatform.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record DevTokenRequest(
        @NotBlank @Email String email,
        String role,
        String name,
        UUID organizationId,
        UUID clubId
) {
}
