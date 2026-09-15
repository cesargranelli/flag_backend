package br.com.flagplatform.round.controller;

import br.com.flagplatform.round.dto.request.CreateRoundRequest;
import br.com.flagplatform.round.dto.request.UpdateRoundRequest;
import br.com.flagplatform.round.dto.response.RoundResponse;
import br.com.flagplatform.round.service.RoundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RoundController implements RoundApi {

    private final RoundService service;

    @Override
    public RoundResponse create(UUID competitionId, CreateRoundRequest request, Authentication authentication) {
        log.info("Recebida requisicao POST /api/v1/competitions/{}/rounds de {}: number={} name={}",
                competitionId, authentication.getName(), request.number(), request.name());
        var requestWithCompetition = new CreateRoundRequest(
                competitionId,
                request.number(),
                request.name(),
                request.type());
        return service.create(requestWithCompetition, authentication.getName());
    }

    @Override
    public List<RoundResponse> findByCompetitionId(UUID competitionId) {
        return service.findByCompetitionId(competitionId);
    }

    @Override
    public RoundResponse findById(UUID id) {
        return service.findById(id);
    }

    @Override
    public RoundResponse update(UUID competitionId, UUID id, UpdateRoundRequest request, Authentication authentication) {
        log.info("Recebida requisicao PUT /api/v1/competitions/{}/rounds/{} de {}: number={} name={}",
                competitionId, id, authentication.getName(), request.number(), request.name());
        var requestWithCompetition = new UpdateRoundRequest(
                competitionId,
                request.number(),
                request.name(),
                request.type());
        return service.update(id, requestWithCompetition, authentication.getName());
    }
}
