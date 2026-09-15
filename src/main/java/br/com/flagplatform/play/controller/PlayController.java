package br.com.flagplatform.play.controller;

import br.com.flagplatform.play.dto.request.CreatePlayRequest;
import br.com.flagplatform.play.dto.response.PlayResponse;
import br.com.flagplatform.play.service.PlayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PlayController implements PlayApi {

    private final PlayService service;

    @Override
    public List<PlayResponse> findByGameId(UUID gameId) {
        return service.findByGameId(gameId);
    }

    @Override
    public PlayResponse create(UUID gameId, CreatePlayRequest request) {
        return service.create(gameId, request);
    }
}
