package br.com.flagplatform.common.security;

/**
 * Expressões SpEL usadas nas anotações @PreAuthorize dos controllers.
 * <p>
 * Centralizadas para evitar duplicação de literais e manter o mapeamento de
 * roles consistente entre os módulos.
 */
public final class SecurityExpressions {

    /**
     * Escrita de dados de gestão (organizações, campeonatos, categorias,
     * campos, times, rodadas e agendamento de jogos).
     */
    public static final String ADMIN_OR_ORGANIZER = "hasAnyRole('ADMIN', 'ORGANIZER', 'ADMIN_LIGA')";

    /**
     * Operação de jogos ao vivo (status e resultado da partida, executada pela mesa ou árbitro).
     */
    public static final String ADMIN_OR_MESA = "hasAnyRole('ADMIN', 'MESA', 'ADMIN_LIGA', 'REFEREE')";

    /**
     * Ações exclusivas de administrador (ex: gestão de usuários).
     */
    public static final String ADMIN = "hasAnyRole('ADMIN', 'ADMIN_LIGA')";

    /**
     * Ações de gestor de clube.
     */
    public static final String CLUB_MANAGER = "hasAnyRole('ADMIN', 'ADMIN_LIGA', 'CLUB_MANAGER')";

    private SecurityExpressions() {
    }
}
