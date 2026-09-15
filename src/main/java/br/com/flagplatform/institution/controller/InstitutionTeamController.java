package br.com.flagplatform.institution.controller;

import br.com.flagplatform.team.dto.request.CreateTeamRequest;
import br.com.flagplatform.team.dto.response.TeamResponse;
import br.com.flagplatform.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InstitutionTeamController implements InstitutionTeamApi {

    private final TeamService teamService;

    @Override
    public TeamResponse createForInstitution(UUID institutionId, CreateTeamRequest request, Authentication authentication) {
        return teamService.createForInstitution(institutionId, request, authentication.getName());
    }

    @Override
    public List<TeamResponse> findByInstitutionId(UUID institutionId) {
        return teamService.findByInstitutionId(institutionId);
    }
}
