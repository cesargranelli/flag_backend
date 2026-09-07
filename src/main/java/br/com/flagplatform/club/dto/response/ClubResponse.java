package br.com.flagplatform.club.dto.response;

import br.com.flagplatform.common.enums.OrganizationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClubResponse(
        UUID id,
        UUID organizationId,
        String name,
        String shortName,
        String sportName,
        String logoUrl,
        String document,
        String documentType,
        String presidentName,
        String presidentCpf,
        OrganizationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
