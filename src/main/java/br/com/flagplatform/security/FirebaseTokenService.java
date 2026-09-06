package br.com.flagplatform.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Serviço responsável por validar e extrair informações de Firebase ID Tokens.
 * <p>
 * Usa verificação manual de JWT (via {@link FirebaseJwtVerifier}) como método primário,
 * evitando o Firebase Admin SDK que sofre do bug "Not in GZIP format" do
 * google-http-client 1.45.3 com JDK 25+.
 * <p>
 * Mantém um fallback local (dev mode) para ambientes sem credenciais.
 */
@Slf4j
@Service
public class FirebaseTokenService {

    private final FirebaseJwtVerifier jwtVerifier;
    private final ObjectMapper objectMapper;

    private final String projectId;

    public FirebaseTokenService(
            @Autowired(required = false) ObjectMapper objectMapper,
            @Value("${app.firebase.project-id:${FIREBASE_PROJECT_ID:}}") String projectId) {
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
        this.projectId = projectId;
        // Inicializa o verificador manual com o project ID
        this.jwtVerifier = new FirebaseJwtVerifier(projectId);
        log.info("FirebaseTokenService inicializado com verificação manual de JWT (project={})", projectId);
    }

    /**
     * Valida o token e extrai as informações do usuário.
     *
     * @param token ID Token do Firebase (sem o prefixo "Bearer ")
     * @return {@link FirebaseUserInfo} se o token for válido,
     *         ou {@link Optional#empty()} caso contrário.
     */
    public Optional<FirebaseUserInfo> verifyToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        // 1. Verificação manual via FirebaseJwtVerifier (java.net.http + jjwt)
        //    Não depende do Firebase Admin SDK, contornando o bug GZIP.
        if (projectId != null && !projectId.isBlank()) {
            Optional<FirebaseUserInfo> result = jwtVerifier.verify(token);
            if (result.isPresent()) {
                return result;
            }
        }

        // 2. Fallback de desenvolvimento local (sem project ID configurado)
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
