package br.com.flagplatform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Verificador manual de Firebase ID Tokens que NÃO depende do Firebase Admin SDK.
 * <p>
 * Utiliza {@link HttpClient} nativo do JDK (não google-http-client) para buscar
 * as chaves públicas do Google e {@code jjwt} para verificar a assinatura RSA.
 * Isso contorna o bug "Not in GZIP format" do google-http-client 1.45.3 com JDK 25+.
 * <p>
 * Referência: https://firebase.google.com/docs/auth/admin/verify-id-tokens
 */
@Slf4j
@Component
public class FirebaseJwtVerifier {

    private static final String CERTS_URL =
            "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";
    private static final String FIREBASE_ISSUER_PREFIX = "https://securetoken.google.com/";
    private static final Duration CERTS_CACHE_TTL = Duration.ofHours(1);

    private final HttpClient httpClient;
    private final String projectId;

    // Cache de chaves públicas (kid -> PublicKey)
    private volatile Map<String, PublicKey> cachedKeys = new ConcurrentHashMap<>();
    private volatile Instant keysExpiry = Instant.MIN;

    public FirebaseJwtVerifier(String projectId) {
        this.projectId = projectId;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Valida um Firebase ID Token e retorna os claims extraídos.
     *
     * @param token Firebase ID Token (sem prefixo "Bearer")
     * @return Optional com um objeto contendo uid, email, name e claims
     */
    public Optional<FirebaseUserInfo> verify(String token) {
        try {
            // 1. Decodifica header sem verificar assinatura para obter o "kid"
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.debug("Token não é um JWT válido (esperado 3 partes, recebeu {})", parts.length);
                return Optional.empty();
            }

            byte[] headerBytes = Base64.getUrlDecoder().decode(parts[0]);
            Map<String, Object> header = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(headerBytes, Map.class);

            String kid = (String) header.get("kid");
            String alg = (String) header.get("alg");
            if (kid == null || !"RS256".equals(alg)) {
                log.debug("Header JWT inesperado: kid={}, alg={}", kid, alg);
                return Optional.empty();
            }

            // 2. Busca chaves públicas (com cache)
            PublicKey publicKey = getPublicKey(kid);
            if (publicKey == null) {
                log.warn("Chave pública não encontrada para kid={}", kid);
                return Optional.empty();
            }

            // 3. Verifica assinatura + claims com jjwt
            Claims claims = Jwts.parser()
                    .verifyWith((RSAPublicKey) publicKey)
                    .requireIssuer(FIREBASE_ISSUER_PREFIX + projectId)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 4. Verifica expiração (jjwt já faz, mas reforçamos)
            Instant exp = claims.getExpiration().toInstant();
            if (exp.isBefore(Instant.now())) {
                log.debug("Token expirado");
                return Optional.empty();
            }

            // 5. Extrai informações
            String uid = claims.getSubject();
            if (uid == null || uid.isBlank()) {
                log.debug("Token sem 'sub' (uid)");
                return Optional.empty();
            }

            String email = claims.get("email", String.class);
            String name = claims.get("name", String.class);

            log.debug("Firebase JWT verificado com sucesso: uid={}, email={}", uid, email);
            return Optional.of(new FirebaseUserInfo(uid, email, name, claims));

        } catch (ExpiredJwtException ex) {
            log.debug("Token JWT expirado: {}", ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            log.warn("Erro ao verificar Firebase JWT manualmente: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private PublicKey getPublicKey(String kid) {
        // Refresh cache se expirado
        if (Instant.now().isAfter(keysExpiry)) {
            refreshKeysCache();
        }
        return cachedKeys.get(kid);
    }

    private synchronized void refreshKeysCache() {
        // Double-check locking
        if (Instant.now().isBefore(keysExpiry)) {
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CERTS_URL))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Falha ao buscar certificados Google: HTTP {}", response.statusCode());
                return;
            }

            Map<String, String> certMap = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(response.body(), Map.class);

            Map<String, PublicKey> newKeys = new ConcurrentHashMap<>();
            for (Map.Entry<String, String> entry : certMap.entrySet()) {
                String x509Pem = entry.getValue();
                PublicKey key = parsePublicKeyFromX509(x509Pem);
                if (key != null) {
                    newKeys.put(entry.getKey(), key);
                }
            }

            cachedKeys = newKeys;
            keysExpiry = Instant.now().plus(CERTS_CACHE_TTL);
            log.info("Certificados Google atualizados: {} chaves carregadas", newKeys.size());
        } catch (Exception ex) {
            log.error("Erro ao atualizar certificados Google: {}", ex.getMessage());
        }
    }

    /**
     * Extrai RSA PublicKey de um certificado X.509 PEM.
     * Não precisamos de BouncyCastle — usamos KeyFactory padrão do JDK.
     */
    private PublicKey parsePublicKeyFromX509(String pem) {
        try {
            // Remove headers/footers do PEM e decodifica
            String base64 = pem
                    .replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replaceAll("\\s", "");

            byte[] certBytes = Base64.getDecoder().decode(base64);

            // Usa JDK padrão para parsear X.509
            java.security.cert.CertificateFactory cf =
                    java.security.cert.CertificateFactory.getInstance("X.509");
            java.security.cert.X509Certificate cert =
                    (java.security.cert.X509Certificate) cf.generateCertificate(
                            new java.io.ByteArrayInputStream(certBytes));

            return cert.getPublicKey();
        } catch (Exception ex) {
            log.trace("Erro ao parsear chave X.509: {}", ex.getMessage());
            return null;
        }
    }
}
