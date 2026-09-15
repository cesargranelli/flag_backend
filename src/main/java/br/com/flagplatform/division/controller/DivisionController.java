package br.com.flagplatform.division.controller;

import br.com.flagplatform.division.dto.request.CreateDivisionRequest;
import br.com.flagplatform.division.dto.request.UpdateDivisionRequest;
import br.com.flagplatform.division.dto.response.DivisionResponse;
import br.com.flagplatform.division.service.DivisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DivisionController implements DivisionApi {

    private final DivisionService service;

    @Override
    public DivisionResponse create(UUID competitionId, CreateDivisionRequest request, Authentication authentication) {
        return service.create(competitionId, request, authentication.getName());
    }

    @Override
    public List<DivisionResponse> findByCompetitionId(UUID competitionId) {
        return service.findByCompetitionId(competitionId);
    }

    @Override
    public DivisionResponse findById(UUID id) {
        return service.findById(id);
    }

    @Override
    public DivisionResponse update(UUID id, UpdateDivisionRequest request, Authentication authentication) {
        return service.update(id, request, authentication.getName());
    }

    @Override
    public void delete(UUID id, Authentication authentication) {
        service.delete(id, authentication.getName());
    }
}
