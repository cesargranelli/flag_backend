package br.com.flagplatform.security;

import br.com.flagplatform.common.enums.UserStatus;
import br.com.flagplatform.user.entity.UserEntity;
import br.com.flagplatform.user.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Filtro que autentica requisições portando um Firebase ID token (Bearer).
 *
 * <p>Fluxo:
 * <ol>
 *   <li>Extrai o token do header {@code Authorization: Bearer <idToken>}</li>
 *   <li>Verifica via {@link FirebaseTokenProvider#verifyIdToken(String)} (Firebase Admin SDK)</li>
 *   <li>Resolve o usuário PostgreSQL via {@code users.firebase_uid}</li>
 *   <li>Carrega role/status do PostgreSQL (source of truth)</li>
 *   <li>Popula o {@link SecurityContextHolder} com {@link UsernamePasswordAuthenticationToken}</li>
 * </ol>
 *
 * <p>Compatibilidade: se a flag {@code app.auth.firebase-enabled} estiver {@code false},
 * o filter é um no-op e o JWT legacy ({@link JwtAuthenticationFilter}) continua valendo.
 *
 * <p>Custom Claims (role, organization_id, club_id, skills) ficam embutidas no token
 * para auditoria; o Spring Security usa as roles do PostgreSQL (mais autoritativo).
 */
@Slf4j
@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
        name = "app.auth.firebase-enabled", havingValue = "true")
@RequiredArgsConstructor
public class FirebaseIdTokenFilter extends OncePerRequestFilter {

    private final FirebaseTokenProvider firebaseTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);
        if (token != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            tryAuthenticate(token);
        }

        filterChain.doFilter(request, response);
    }

    private void tryAuthenticate(String idToken) {
        FirebaseToken decoded = firebaseTokenProvider.verifyIdToken(idToken);
        if (decoded == null) {
            return;
        }

        String firebaseUid = decoded.getUid();
        UserEntity user = userRepository.findByFirebaseUid(firebaseUid).orElse(null);
        if (user == null) {
            // Usuário não vinculado — pode estar em fase de migração. Ignora silenciosamente
            // para não bloquear o JWT legacy (que autentica por email) ou causar 401 indevido.
            log.debug("Firebase UID '{}' autenticado, porém sem vínculo no PostgreSQL", firebaseUid);
            return;
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            log.debug("Usuário '{}' vinculado ao Firebase UID '{}' não está ACTIVE (status={})",
                    user.getEmail(), firebaseUid, user.getStatus());
            throw new UsernameNotFoundException(
                    "User with email '%s' is not active".formatted(user.getEmail()));
        }

        List<SimpleGrantedAuthority> authorities = user.getRole() != null
                ? List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getCode()))
                : List.of();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

        // Custom claims do Firebase ficam disponíveis no principal para auditoria / debug.
        // Mantemos um map leve (não persistimos o token inteiro no contexto).
        Map<String, Object> claims = decoded.getClaims();
        authentication.setDetails(claims);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}