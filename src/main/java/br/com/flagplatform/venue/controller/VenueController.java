package br.com.flagplatform.venue.controller;

import br.com.flagplatform.venue.dto.request.CreateVenueRequest;
import br.com.flagplatform.venue.dto.request.UpdateVenueRequest;
import br.com.flagplatform.venue.dto.response.VenueResponse;
import br.com.flagplatform.venue.service.VenueService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class VenueController implements VenueApi {

    private final VenueService service;

    @Override
    public VenueResponse create(CreateVenueRequest request) {
        return service.create(request);
    }

    @Override
    public List<VenueResponse> findAll(int page, int size, HttpServletResponse response) {
        var result = service.findAll(page, size);
        response.setHeader("X-Total-Count", String.valueOf(result.total()));
        return result.items();
    }

    @Override
    public VenueResponse findById(UUID id) {
        return service.findById(id);
    }

    @Override
    public VenueResponse update(UUID id, UpdateVenueRequest request) {
        return service.update(id, request);
    }
}
