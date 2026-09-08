package br.com.flagplatform.team.dto.request;

import br.com.flagplatform.common.enums.CompetitionTeamStatus;

public record UpdateCompetitionTeamRequest(
        CompetitionTeamStatus status,
        String groupName,
        String conferenceName,
        String divisionName,
        Integer seedNumber
) {
}
