package br.com.flagplatform.affiliation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AffiliationWindowResponse(
        UUID id,
        UUID organizationId,
        String organizationName,
        String season,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        boolean isOpen,
        String instructions,
        LocalDateTime createdAt,
        String createdBy
) {}
