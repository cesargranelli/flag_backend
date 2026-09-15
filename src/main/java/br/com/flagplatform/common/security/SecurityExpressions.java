package br.com.flagplatform.common.security;

/**
 * Expressões SpEL usadas nas anotações @PreAuthorize dos controllers.
 * <p>
 * Centralizadas para evitar duplicação de literais e manter o mapeamento de
 * roles consistente entre os módulos.
 *
 * <p>Roles válidas (UserRole enum):
 * ADMIN, ORGANIZER, COMMISSIONER, REFEREE, MANAGER, FAN.</p>
 */
public final class SecurityExpressions {

    /**
     * Escrita de dados de gestão (organizações, campeonatos, categorias,
     * campos, times, rodadas e agendamento de jogos).
     */
    public static final String ADMIN_OR_ORGANIZER =
            "hasAuthority('STATUS_ACTIVE') and hasAnyRole('ADMIN', 'ORGANIZER')";

    /**
     * Operação de jogos ao vivo (status e resultado da partida, executada pela comissão/ábitro).
     */
    public static final String ADMIN_OR_COMMISSIONER =
            "hasAuthority('STATUS_ACTIVE') and hasAnyRole('ADMIN', 'COMMISSIONER', 'REFEREE')";

    /**
     * Ações exclusivas de administrador da plataforma (ex: gestão de usuários).
     */
    public static final String ADMIN =
            "hasAuthority('STATUS_ACTIVE') and hasRole('ADMIN')";

    /**
     * Ações de gestor de clube ou universidade.
     */
    public static final String MANAGER =
            "hasAuthority('STATUS_ACTIVE') and hasAnyRole('ADMIN', 'MANAGER')";

    public static final String ORGANIZATION_WRITE =
            "hasAuthority('STATUS_ACTIVE') and hasAnyRole('ORGANIZER', 'ADMIN')";

    public static final String INSTITUTION_WRITE =
            "hasAuthority('STATUS_ACTIVE') and hasAnyRole('ORGANIZER', 'MANAGER', 'ADMIN')";

    /**
     * Exige usuário com status ACTIVE (bloqueia PENDING de escrita).
     */
    public static final String ACTIVE = "hasAuthority('STATUS_ACTIVE')";

    private SecurityExpressions() {
    }
}
