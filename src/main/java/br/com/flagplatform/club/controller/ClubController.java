package br.com.flagplatform.club.controller;

import br.com.flagplatform.club.dto.request.CreateClubRequest;
import br.com.flagplatform.club.dto.request.UpdateClubRequest;
import br.com.flagplatform.club.dto.response.ClubResponse;
import br.com.flagplatform.club.service.ClubService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClubController implements ClubApi {

    private final ClubService service;

    @Override
    public ClubResponse create(UUID organizationId, CreateClubRequest request) {
        return service.create(organizationId, request);
    }

    @Override
    public List<ClubResponse> findByOrganization(UUID organizationId, int page, int size, HttpServletResponse response) {
        var result = service.findByOrganization(organizationId, page, size);
        response.setHeader("X-Total-Count", String.valueOf(result.total()));
        return result.items();
    }

    @Override
    public ClubResponse findById(UUID id) {
        return service.findById(id);
    }

    @Override
    public ClubResponse update(UUID id, UpdateClubRequest request) {
        return service.update(id, request);
    }
}
