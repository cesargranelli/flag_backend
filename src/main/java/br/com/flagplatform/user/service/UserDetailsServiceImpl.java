package br.com.flagplatform.user.service;

import br.com.flagplatform.common.enums.UserStatus;
import br.com.flagplatform.user.entity.UserEntity;
import br.com.flagplatform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with email '%s' not found".formatted(email)));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UsernameNotFoundException(
                    "User with email '%s' is not active".formatted(email));
        }

        // Senha não é utilizada — autenticação é via Firebase Auth SDK no frontend.
        // Spring Security exige uma senha no UserDetails; usamos string vazia.
        return User.withUsername(user.getEmail())
                .password("")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getCode())))
                .build();
    }

}
