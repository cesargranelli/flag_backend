package br.com.flagplatform.security;

import br.com.flagplatform.user.TokenProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provedor de tokens para Firebase Auth. Mantém a interface {@link TokenProvider} para
 * compatibilidade durante a transição do JWT custom (HS256) → Firebase ID tokens.
 *
 * <p><b>Importante:</b> Firebase emite ID tokens para o <i>cliente</i> via Firebase SDK
 * (após signIn com email/password). O backend apenas verifica e mapeia claims; portanto
 * {@link #generateToken(String)} lança {@link UnsupportedOperationException} — o cliente
 * deve obter o ID token via Firebase SDK no app.
 *
 * <p>Custom Claims são populadas <i>no backend</i> usando {@link #setCustomClaims} e
 * embutidas no próximo ID token emitido para o usuário (após refresh do token no cliente).
 *
 * @see FirebaseIdTokenFilter
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.auth.firebase-enabled", havingValue = "true")
public class FirebaseTokenProvider implements TokenProvider {

    private final FirebaseAuth firebaseAuth;

    public FirebaseTokenProvider(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public String generateToken(String subject) {
        throw new UnsupportedOperationException(
                "Token generation moved to Firebase Auth SDK on client. "
                        + "Backend verifies ID tokens, not generates them.");
    }

    /**
     * O ID token é gerado pelo Firebase SDK no cliente (vida ~1h). Este valor reflete o
     * padrão do Firebase; em produção os clients obtêm o token via
     * {@code firebase_auth.currentUser?.getIdToken()}.
     */
    @Override
    public long getExpirationSeconds() {
        return 3600L;
    }

    /**
     * Verifica um ID token do Firebase. Retorna o token decodificado ou {@code null}
     * quando inválido/expirado/revogado.
     *
     * @param idToken token enviado no header Authorization: Bearer ...
     * @return {@link FirebaseToken} decodificado ou {@code null} se inválido
     */
    public FirebaseToken verifyIdToken(String idToken) {
        try {
            return firebaseAuth.verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            log.debug("Firebase ID token inválido: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Constrói o mapa de custom claims a partir do {@link UserPrincipal}.
     * Limite: 1000 bytes (regra do Firebase). Mantemos claims minimalistas.
     */
    public Map<String, Object> buildCustomClaims(
            String role,
            String email,
            String organizationId,
            String clubId,
            List<String> skills) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("email", email);
        if (organizationId != null) {
            claims.put("organization_id", organizationId);
        }
        if (clubId != null) {
            claims.put("club_id", clubId);
        }
        if (skills != null) {
            claims.put("skills", skills);
        }
        return claims;
    }

    /**
     * Persiste as custom claims para um Firebase UID. O ID token do cliente precisará
     * ser refrescado (signOut/signIn) para incorporar as novas claims.
     */
    public void setCustomClaims(String firebaseUid, Map<String, Object> claims)
            throws FirebaseAuthException {
        firebaseAuth.setCustomUserClaims(firebaseUid, claims);
    }

    /**
     * Resolve o Firebase UID a partir do email. Útil no fluxo de migração de usuários
     * existentes (vincula user do PostgreSQL → Firebase UID).
     */
    public String resolveUidByEmail(String email) throws FirebaseAuthException {
        UserRecord record = firebaseAuth.getUserByEmail(email);
        return record.getUid();
    }
}