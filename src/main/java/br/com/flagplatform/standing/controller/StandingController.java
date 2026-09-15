package br.com.flagplatform.standing.controller;

import br.com.flagplatform.standing.dto.response.StandingResponse;
import br.com.flagplatform.standing.service.StandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StandingController implements StandingApi {

    private final StandingService service;

    @Override
    public List<StandingResponse> findByCompetitionId(UUID competitionId) {
        return service.findByCompetitionId(competitionId);
    }
}
