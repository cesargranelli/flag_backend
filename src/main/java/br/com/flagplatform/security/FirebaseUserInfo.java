package br.com.flagplatform.security;

import java.util.Map;

/**
 * Dados extraídos de um Firebase ID Token após validação.
 */
public record FirebaseUserInfo(
        String uid,
        String email,
        String name,
        Map<String, Object> claims
) {
}
