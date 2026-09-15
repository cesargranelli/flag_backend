package br.com.flagplatform.competition.controller;

import br.com.flagplatform.competition.dto.request.CreateCompetitionEnrollmentWindowRequest;
import br.com.flagplatform.competition.dto.response.CompetitionEnrollmentWindowResponse;
import br.com.flagplatform.competition.service.CompetitionEnrollmentWindowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CompetitionEnrollmentWindowController implements CompetitionEnrollmentWindowApi {

    private final CompetitionEnrollmentWindowService service;

    @Override
    public CompetitionEnrollmentWindowResponse openWindow(
            UUID competitionId,
            CreateCompetitionEnrollmentWindowRequest request,
            Authentication authentication) {
        return service.openWindow(competitionId, request, authentication.getName());
    }

    @Override
    public CompetitionEnrollmentWindowResponse closeWindow(UUID competitionId) {
        return service.closeWindow(competitionId);
    }

    @Override
    public CompetitionEnrollmentWindowResponse findByCompetitionId(UUID competitionId) {
        return service.findByCompetitionId(competitionId);
    }

    @Override
    public List<CompetitionEnrollmentWindowResponse> listOpenWindows() {
        return service.listOpenWindows();
    }
}
