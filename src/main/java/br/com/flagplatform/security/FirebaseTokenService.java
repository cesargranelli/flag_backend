package br.com.flagplatform.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Serviço responsável por validar e extrair informações de Firebase ID Tokens.
 * <p>
 * Quando o {@link FirebaseAuth} está inicializado com credenciais válidas,
 * realiza a verificação criptográfica estrita da assinatura com o Google.
 * Caso o SDK não esteja configurado (ambiente local de desenvolvimento/testes),
 * opera em modo fallback tolerante inspecionando o payload do JWT.
 */
@Slf4j
@Service
public class FirebaseTokenService {

    private final FirebaseAuth firebaseAuth;
    private final ObjectMapper objectMapper;

    public FirebaseTokenService(
            @Autowired(required = false) FirebaseAuth firebaseAuth,
            @Autowired(required = false) ObjectMapper objectMapper) {
        this.firebaseAuth = firebaseAuth;
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    public boolean isFirebaseConfigured() {
        return firebaseAuth != null;
    }

    /**
     * Valida o token e extrai as informações do usuário.
     *
     * @param token ID Token do Firebase (sem o prefixo "Bearer ")
     * @return {@link FirebaseUserInfo} se o token for válido e corresponder a um token Firebase,
     *         ou {@link Optional#empty()} caso contrário.
     */
    public Optional<FirebaseUserInfo> verifyToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        // 1. Verificação oficial via Firebase Admin SDK
        if (firebaseAuth != null) {
            try {
                FirebaseToken decoded = firebaseAuth.verifyIdToken(token);
                return Optional.of(new FirebaseUserInfo(
                        decoded.getUid(),
                        decoded.getEmail(),
                        decoded.getName(),
                        decoded.getClaims()
                ));
            } catch (FirebaseAuthException ex) {
                log.debug("Falha na validação do Firebase ID Token via Admin SDK: {}", ex.getMessage());
                return Optional.empty();
            } catch (Exception ex) {
                log.warn("Erro inesperado ao validar Firebase ID Token: {}", ex.getMessage());
                return Optional.empty();
            }
        }

        // 2. Fallback de desenvolvimento local (sem credenciais de service account)
        return parseDevFallbackToken(token);
    }

    private Optional<FirebaseUserInfo> parseDevFallbackToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return Optional.empty();
        }

        try {
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode payload = objectMapper.readTree(payloadBytes);

            // Verifica se possui claims característicos do Firebase ID Token
            boolean isFirebase = payload.has("user_id")
                    || (payload.has("iss") && payload.get("iss").asText().contains("securetoken.google.com"))
                    || (payload.has("firebase"));

            if (!isFirebase && !payload.has("sub")) {
                return Optional.empty();
            }

            String uid = payload.has("user_id")
                    ? payload.get("user_id").asText()
                    : (payload.has("sub") ? payload.get("sub").asText() : null);

            if (uid == null || uid.isBlank()) {
                return Optional.empty();
            }

            String email = payload.has("email") ? payload.get("email").asText() : null;
            String name = payload.has("name") ? payload.get("name").asText() : null;

            Map<String, Object> claims = Collections.emptyMap();
            try {
                claims = objectMapper.convertValue(payload, new TypeReference<Map<String, Object>>() {});
            } catch (Exception ignored) {
            }

            log.info("Dev Mode: Firebase token parseado em modo fallback local. UID: {}, Email: {}", uid, email);
            return Optional.of(new FirebaseUserInfo(uid, email, name, claims));
        } catch (Exception ex) {
            log.trace("Não foi possível parsear token como JWT do Firebase: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
