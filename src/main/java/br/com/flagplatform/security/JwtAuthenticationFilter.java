package br.com.flagplatform.security;

import br.com.flagplatform.common.enums.UserStatus;
import br.com.flagplatform.user.entity.UserEntity;
import br.com.flagplatform.user.repository.UserRepository;
import br.com.flagplatform.user.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filtro que autentica requisições com Firebase ID Token ou token JWT legado no cabeçalho Authorization.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final FirebaseTokenService firebaseTokenService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 1. Tenta autenticar via Firebase ID Token
            Optional<FirebaseUserInfo> firebaseUserOpt = firebaseTokenService.verifyToken(token);
            if (firebaseUserOpt.isPresent()) {
                try {
                    FirebaseUserInfo firebaseUser = firebaseUserOpt.get();
                    UserEntity user = authService.getOrProvisionFirebaseUser(firebaseUser);

                    if (user != null && user.getStatus() == UserStatus.ACTIVE) {
                        UserPrincipal principal = new UserPrincipal(user);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        principal, null, principal.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception ex) {
                    log.warn("Erro ao autenticar usuário com Firebase ID Token: {}", ex.getMessage());
                }
            } else if (tokenProvider.isValid(token)) {
                // 2. Fallback para token JWT local (HS256) legado
                try {
                    String email = tokenProvider.extractEmail(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (UsernameNotFoundException ex) {
                    // Token válido, mas o usuário não existe mais: não autentica.
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

}
