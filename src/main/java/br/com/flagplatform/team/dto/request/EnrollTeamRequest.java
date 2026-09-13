package br.com.flagplatform.team.dto.request;

import br.com.flagplatform.common.enums.CompetitionTeamStatus;

/**
 * Corpo opcional da inscrição de um time em uma competição
 * (POST /api/v1/competitions/{competitionId}/teams/{teamId}).
 */
public record EnrollTeamRequest(
        CompetitionTeamStatus status,
        String groupName,
        String conferenceName,
        String divisionName,
        Integer seedNumber
) {
}
