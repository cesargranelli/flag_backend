package br.com.flagplatform.competition.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CompetitionEnrollmentWindowResponse(
        UUID id,
        UUID competitionId,
        String competitionName,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        boolean isOpen,
        String instructions,
        LocalDateTime createdAt,
        String createdBy
) {}
