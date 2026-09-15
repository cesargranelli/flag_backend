package br.com.flagplatform.conference.controller;

import br.com.flagplatform.conference.dto.request.CreateConferenceRequest;
import br.com.flagplatform.conference.dto.request.UpdateConferenceRequest;
import br.com.flagplatform.conference.dto.response.ConferenceResponse;
import br.com.flagplatform.conference.service.ConferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ConferenceController implements ConferenceApi {

    private final ConferenceService service;

    @Override
    public ConferenceResponse create(UUID competitionId, CreateConferenceRequest request, Authentication authentication) {
        return service.create(competitionId, request, authentication.getName());
    }

    @Override
    public List<ConferenceResponse> findByCompetitionId(UUID competitionId) {
        return service.findByCompetitionId(competitionId);
    }

    @Override
    public ConferenceResponse findById(UUID id) {
        return service.findById(id);
    }

    @Override
    public ConferenceResponse update(UUID id, UpdateConferenceRequest request, Authentication authentication) {
        return service.update(id, request, authentication.getName());
    }

    @Override
    public void delete(UUID id, Authentication authentication) {
        service.delete(id, authentication.getName());
    }
}
