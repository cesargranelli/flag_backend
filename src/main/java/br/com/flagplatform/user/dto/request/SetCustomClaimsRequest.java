package br.com.flagplatform.user.dto.request;

import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Request para definir Custom Claims do Firebase para um usuário.
 *
 * <p>Os valores são persistidos tanto no Firebase (custom claims) quanto no
 * PostgreSQL (para manter a source of truth local).
 *
 * <p>Estrutura de Custom Claims gerada:
 * <pre>{@code
 * {
 *   "role": "ORG_ADMIN",
 *   "organization_id": "uuid",
 *   "club_id": "uuid",
 *   "skills": ["athlete", "coach"]
 * }
 * }</pre>
 *
 * @param role            Papel do usuário no contexto Firebase (SUPER_ADMIN, ORG_ADMIN, MANAGER, USER)
 * @param organizationId  ID da organização (obrigatório para ORG_ADMIN)
 * @param clubId          ID do clube (obrigatório para MANAGER)
 * @param skills          Skills do usuário (athlete, coach, referee, manager)
 */
public record SetCustomClaimsRequest(
        @Size(max = 50)
        String role,

        UUID organizationId,

        UUID clubId,

        @Size(max = 10, message = "Maximum 10 skills allowed")
        List<String> skills
) {
}
