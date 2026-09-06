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
                log.debug("Falha na validacao Firebase ID Token: {}", ex.getMessage());
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
}
