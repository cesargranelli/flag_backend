package br.com.flagplatform.security;

import br.com.flagplatform.common.enums.UserRole;
import br.com.flagplatform.common.enums.UserStatus;
import br.com.flagplatform.user.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Representação do usuário autenticado no {@link org.springframework.security.core.context.SecurityContextHolder}.
 * <p>
 * Carrega a identidade do Firebase (firebaseUid) e os dados de domínio e autorização
 * do PostgreSQL (id, organizationId, clubId, role).
 */
@Getter
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String name;
    private final String firebaseUid;
    private final UUID organizationId;
    private final UUID clubId;
    private final UserRole role;
    private final UserStatus status;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(UserEntity user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.firebaseUid = user.getFirebaseUid();
        this.organizationId = user.getOrganizationId();
        this.clubId = user.getClubId();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.authorities = buildAuthorities(user.getRole(), user.getStatus());
    }

    private static List<SimpleGrantedAuthority> buildAuthorities(UserRole role, UserStatus status) {
        var list = new java.util.ArrayList<SimpleGrantedAuthority>();
        if (role != null) list.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
        if (status != null) list.add(new SimpleGrantedAuthority("STATUS_" + status.name()));
        return List.copyOf(list);
    }

    public UserPrincipal(UUID id, String email, String name, String firebaseUid,
                          UUID organizationId, UUID clubId, UserRole role, UserStatus status) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.firebaseUid = firebaseUid;
        this.organizationId = organizationId;
        this.clubId = clubId;
        this.role = role;
        this.status = status;
        this.authorities = buildAuthorities(role, status);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email != null ? email : (firebaseUid != null ? firebaseUid : "");
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
