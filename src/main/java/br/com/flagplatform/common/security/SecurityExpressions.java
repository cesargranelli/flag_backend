package br.com.flagplatform.common.security;

/**
 * Expressões SpEL usadas nas anotações @PreAuthorize dos controllers.
 * <p>
 * Centralizadas para evitar duplicação de literais e manter o mapeamento de
 * roles consistente entre os módulos.
 * 
 * Roles: SUPER_ADMIN > ORG_ADMIN > MANAGER > USER
 */
public final class SecurityExpressions {

    /**
     * Escrita de dados de gestão (organizações, clubes, campeonatos, categorias,
     * campos, times, rodadas e agendamento de jogos).
     * SUPER_ADMIN e ORG_ADMIN podem criar/editar conteúdo de gestão.
     */
    public static final String ADMIN_OR_ORGANIZER = "hasAnyRole('SUPER_ADMIN', 'ORG_ADMIN')";

    /**
     * Operação de jogos ao vivo (status e resultado da partida, executada pela mesa).
     * Inclui MANAGER para permitir operação delegada dentro do clube.
     */
    public static final String ADMIN_OR_MESA = "hasAnyRole('SUPER_ADMIN', 'ORG_ADMIN', 'MANAGER')";

    /**
     * Ações exclusivas de administrador (ex: gestão de usuários, aprovação de contas).
     * Apenas SUPER_ADMIN.
     */
    public static final String ADMIN = "hasRole('SUPER_ADMIN')";

    private SecurityExpressions() {
    }
}
