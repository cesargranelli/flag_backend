package br.com.flagplatform.game.controller;

import br.com.flagplatform.game.dto.request.*;
import br.com.flagplatform.game.dto.response.*;
import br.com.flagplatform.game.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GameController implements GameApi {

    private final GameService service;

    @Override
    public List<LiveGameResponse> findLiveGames() {
        return service.findLiveGames();
    }

    @Override
    public GameResponse create(CreateGameRequest request, Authentication authentication) {
        return service.create(request, authentication.getName());
    }

    @Override
    public GameBatchResponse createBatch(UUID roundId, GameBatchRequest request, Authentication authentication) {
        return service.createBatch(roundId, request, authentication.getName());
    }

    @Override
    public List<GameSummaryResponse> findByRoundId(UUID roundId) {
        return service.findByRoundId(roundId);
    }

    @Override
    public List<GameSummaryResponse> findByCompetitionId(UUID competitionId) {
        return service.findByCompetitionId(competitionId);
    }

    @Override
    public GameResponse findById(UUID id) {
        return service.findById(id);
    }

    @Override
    public GameResponse update(UUID id, UpdateGameRequest request, Authentication authentication) {
        return service.update(id, request, authentication.getName());
    }

    @Override
    public GameResponse updateStatus(UUID id, UpdateGameStatusRequest request) {
        return service.updateStatus(id, request);
    }

    @Override
    public GameResponse registerResult(UUID id, RegisterGameResultRequest request) {
        return service.registerResult(id, request);
    }

    @Override
    public GameResponse addScoreEvent(UUID id, AddScoreEventRequest request) {
        return service.registerScoreEvent(id, request);
    }

    @Override
    public GameResponse correctScore(UUID id, UpdateScoreRequest request) {
        return service.correctScore(id, request);
    }

    @Override
    public List<ScoreEventResponse> listScoreEvents(UUID id) {
        return service.listScoreEvents(id);
    }
}
