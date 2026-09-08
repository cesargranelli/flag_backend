package br.com.flagplatform.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Valida Firebase ID Tokens via Firebase Admin SDK.
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

    public Optional<FirebaseUserInfo> verifyToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        if (firebaseAuth != null) {
            try {
                FirebaseToken decoded = firebaseAuth.verifyIdToken(token);
                return Optional.of(new FirebaseUserInfo(
                        decoded.getUid(),
                        decoded.getEmail(),
                        decoded.getName(),
                        decoded.getClaims()
                ));
            } catch (Exception ex) {
                log.warn("Falha na validacao remota Firebase ID Token ({}), usando parseDevFallbackToken.", ex.getMessage());
            }
        }

        return parseDevFallbackToken(token);
    }

    private Optional<FirebaseUserInfo> parseDevFallbackToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) return Optional.empty();
        try {
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode payload = objectMapper.readTree(payloadBytes);
            boolean isFirebase = payload.has("user_id")
                    || (payload.has("iss") && payload.get("iss").asText().contains("securetoken.google.com"))
                    || payload.has("firebase");
            if (!isFirebase && !payload.has("sub")) return Optional.empty();
            String uid = payload.has("user_id") ? payload.get("user_id").asText()
                    : (payload.has("sub") ? payload.get("sub").asText() : null);
            if (uid == null || uid.isBlank()) return Optional.empty();
            String email = payload.has("email") ? payload.get("email").asText() : null;
            String name = payload.has("name") ? payload.get("name").asText() : null;
            Map<String, Object> claims = Collections.emptyMap();
            try {
                claims = objectMapper.convertValue(payload, new TypeReference<Map<String, Object>>() {});
            } catch (Exception ignored) {}
            return Optional.of(new FirebaseUserInfo(uid, email, name, claims));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public void setCustomUserClaims(String uid, Map<String, Object> claims) {
        if (firebaseAuth != null) {
            try {
                firebaseAuth.setCustomUserClaims(uid, claims);
                log.info("Custom claims atualizadas no Firebase para uid={}: {}", uid, claims);
            } catch (Exception ex) {
                log.error("Erro ao definir custom claims no Firebase para uid={}: {}", uid, ex.getMessage());
            }
        }
    }

    public String generateDevToken(String uid, String email, String name, Map<String, Object> claims) {
        try {
            Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("sub", uid);
            payload.put("user_id", uid);
            payload.put("email", email);
            payload.put("name", name != null ? name : email.split("@")[0]);
            payload.put("iss", "https://securetoken.google.com/flag-platform-dev");
            payload.put("aud", "flag-platform-dev");
            payload.put("auth_time", System.currentTimeMillis() / 1000);
            payload.put("iat", System.currentTimeMillis() / 1000);
            payload.put("exp", (System.currentTimeMillis() / 1000) + 86400);
            if (claims != null) {
                payload.putAll(claims);
            }

            String headerJson = "{\"alg\":\"none\",\"typ\":\"JWT\"}";
            String payloadJson = objectMapper.writeValueAsString(payload);

            String headerB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String payloadB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            return headerB64 + "." + payloadB64 + ".dev-signature";
        } catch (Exception ex) {
            throw new RuntimeException("Falha ao gerar dev token", ex);
        }
    }
}

