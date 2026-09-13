package br.com.flagplatform.affiliation.dto;

import br.com.flagplatform.common.enums.AffiliationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record AffiliationResponse(
        UUID id,
        UUID institutionId,
        String institutionName,
        String institutionType,
        String institutionLogoUrl,
        UUID organizationId,
        String organizationName,
        String organizationType,
        String season,
        AffiliationStatus status,
        String rejectionReason,
        LocalDateTime requestedAt,
        String requestedBy,
        LocalDateTime reviewedAt,
        String reviewedBy
) {}
