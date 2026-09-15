package br.com.flagplatform.team.controller;

import br.com.flagplatform.team.dto.request.CreateTeamRequest;
import br.com.flagplatform.team.dto.request.EnrollTeamRequest;
import br.com.flagplatform.team.dto.request.UpdateCompetitionTeamRequest;
import br.com.flagplatform.team.dto.request.UpdateTeamRequest;
import br.com.flagplatform.team.dto.response.CompetitionTeamResponse;
import br.com.flagplatform.team.dto.response.TeamResponse;
import br.com.flagplatform.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TeamController implements TeamApi {

    private final TeamService service;

    @Override
    public TeamResponse create(UUID organizationId, CreateTeamRequest request, Authentication authentication) {
        return service.create(organizationId, request, authentication.getName());
    }

    @Override
    public List<TeamResponse> findByOrganizationId(UUID organizationId) {
        return service.findByOrganizationId(organizationId);
    }

    @Override
    public List<TeamResponse> findAll() {
        return service.findAll();
    }

    @Override
    public TeamResponse findById(UUID id) {
        return service.findById(id);
    }

    @Override
    public TeamResponse update(UUID id, UpdateTeamRequest request, Authentication authentication) {
        return service.update(id, request, authentication.getName());
    }

    @Override
    public void delete(UUID id, Authentication authentication) {
        service.delete(id, authentication.getName());
    }

    @Override
    public void deactivate(UUID id, Authentication authentication) {
        service.deactivate(id, authentication.getName());
    }

    @Override
    public void reactivate(UUID id, Authentication authentication) {
        service.reactivate(id, authentication.getName());
    }

    @Override
    public CompetitionTeamResponse enrollInCompetition(UUID competitionId, UUID teamId, EnrollTeamRequest request, Authentication authentication) {
        return service.enrollInCompetition(competitionId, teamId, request, authentication.getName());
    }

    @Override
    public CompetitionTeamResponse updateAllocation(UUID competitionId, UUID teamId, UpdateCompetitionTeamRequest request, Authentication authentication) {
        return service.updateAllocation(competitionId, teamId, request, authentication.getName());
    }

    @Override
    public CompetitionTeamResponse approveTeam(UUID competitionId, UUID teamId, Authentication authentication) {
        return service.approveTeam(competitionId, teamId, authentication.getName());
    }

    @Override
    public CompetitionTeamResponse rejectTeam(UUID competitionId, UUID teamId, Authentication authentication) {
        return service.rejectTeam(competitionId, teamId, authentication.getName());
    }

    @Override
    public List<CompetitionTeamResponse> findByCompetitionId(UUID competitionId) {
        return service.findByCompetitionId(competitionId);
    }

    @Override
    public List<CompetitionTeamResponse> findCompetitionsByTeamId(UUID teamId) {
        return service.findByTeamId(teamId);
    }

    @Override
    public void removeFromCompetition(UUID competitionId, UUID teamId, Authentication authentication) {
        service.removeFromCompetition(competitionId, teamId, authentication.getName());
    }
}
