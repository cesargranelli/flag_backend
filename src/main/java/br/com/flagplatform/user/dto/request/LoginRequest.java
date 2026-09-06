package br.com.flagplatform.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String firebaseIdToken
) {
}
