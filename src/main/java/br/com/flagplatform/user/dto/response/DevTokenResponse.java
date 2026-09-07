package br.com.flagplatform.user.dto.response;

public record DevTokenResponse(
        String token,
        UserResponse user
) {
}
