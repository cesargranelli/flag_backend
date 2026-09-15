package br.com.flagplatform.roster.controller;

import br.com.flagplatform.roster.dto.request.AddRosterEntryRequest;
import br.com.flagplatform.roster.dto.request.RosterBatchRequest;
import br.com.flagplatform.roster.dto.response.RosterBatchResponse;
import br.com.flagplatform.roster.dto.response.RosterEntryResponse;
import br.com.flagplatform.roster.dto.response.RosterResponse;
import br.com.flagplatform.roster.service.RosterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RosterController implements RosterApi {

    private final RosterService service;

    @Override
    public RosterEntryResponse add(UUID teamId, UUID competitionId, AddRosterEntryRequest request, Authentication authentication) {
        return service.add(teamId, competitionId, request, authentication.getName());
    }

    @Override
    public RosterBatchResponse createBatch(UUID teamId, UUID competitionId, RosterBatchRequest request, Authentication authentication) {
        return service.createBatch(teamId, competitionId, request, authentication.getName());
    }

    @Override
    public List<RosterResponse> findRostersByTeam(UUID teamId) {
        return service.findByTeamId(teamId);
    }

    @Override
    public List<RosterEntryResponse> findRosterByTeamAndCompetition(UUID teamId, UUID competitionId) {
        return service.findRosterByTeamAndCompetition(teamId, competitionId);
    }

    @Override
    public void remove(UUID teamId, UUID competitionId, UUID athleteId, Authentication authentication) {
        service.remove(teamId, competitionId, athleteId, authentication.getName());
    }

    @Override
    public void deactivate(UUID teamId, UUID competitionId, Authentication authentication) {
        service.deactivate(teamId, competitionId, authentication.getName());
    }

    @Override
    public void reactivate(UUID teamId, UUID competitionId, Authentication authentication) {
        service.reactivate(teamId, competitionId, authentication.getName());
    }

    @Override
    public List<RosterEntryResponse> findBaseRoster(UUID teamId) {
        return service.findBaseRoster(teamId);
    }

    @Override
    public RosterEntryResponse addToBaseRoster(UUID teamId, AddRosterEntryRequest request, Authentication authentication) {
        return service.addToBaseRoster(teamId, request, authentication.getName());
    }

    @Override
    public void removeFromBaseRoster(UUID teamId, UUID athleteId, Authentication authentication) {
        service.removeFromBaseRoster(teamId, athleteId, authentication.getName());
    }
}
