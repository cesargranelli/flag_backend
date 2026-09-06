package br.com.flagplatform.club;

import br.com.flagplatform.common.enums.OrganizationStatus;

import java.util.UUID;

/**
 * Projeção pública de um clube para outros módulos.
 * <p>
 * Fica na raiz do módulo para não vazar API interna (entidade/DTO) e manter
 * o isolamento do Spring Modulith.
 */
public record ClubInfo(
        UUID id,
        UUID organizationId,
        String name,
        String shortName,
        String sportName,
        String logoUrl,
        OrganizationStatus status
) {
}
