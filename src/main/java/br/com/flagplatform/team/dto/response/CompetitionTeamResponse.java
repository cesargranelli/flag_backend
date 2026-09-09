package br.com.flagplatform.team.dto.response;

import br.com.flagplatform.common.enums.CompetitionTeamStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompetitionTeamResponse(
        UUID id,
        UUID competitionId,
        UUID teamId,
        String teamName,
        String teamShortName,
        String teamLogoUrl,
        UUID organizationId,
        String organizationName,
        UUID clubId,
        String clubName,
        CompetitionTeamStatus status,
        String groupName,
        String conferenceName,
        String divisionName,
        Integer seedNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
