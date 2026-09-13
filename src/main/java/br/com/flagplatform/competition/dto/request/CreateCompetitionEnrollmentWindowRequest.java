package br.com.flagplatform.competition.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateCompetitionEnrollmentWindowRequest(
        @NotBlank String title,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        String instructions
) {}
