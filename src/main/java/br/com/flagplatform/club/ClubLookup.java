package br.com.flagplatform.club;

import java.util.UUID;

/**
 * API pública do módulo club para consulta de clubes.
 * <p>
 * Sem tipos de subpacote (DTO/exception) na assinatura para não vazar
 * API interna e manter o isolamento do Spring Modulith.
 */
public interface ClubLookup {

    void assertExists(UUID id);

    boolean existsById(UUID id);

    ClubInfo findClubInfoById(UUID id);

    UUID findOrganizationIdByClubId(UUID id);

}
