package br.com.flagplatform.checkin.controller;

import br.com.flagplatform.checkin.dto.request.CheckInStatusRequest;
import br.com.flagplatform.checkin.dto.request.MatchNumberRequest;
import br.com.flagplatform.checkin.dto.response.CheckInResponse;
import br.com.flagplatform.checkin.dto.response.ValidationResponse;
import br.com.flagplatform.checkin.service.CheckInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CheckInController implements CheckInApi {

    private final CheckInService service;

    @Override
    public List<CheckInResponse> getCheckinList(UUID gameId) {
        return service.getCheckinList(gameId);
    }

    @Override
    public CheckInResponse checkin(UUID gameId, UUID athleteId, CheckInStatusRequest request, UserDetails principal) {
        return service.checkin(gameId, athleteId, request, principal.getUsername());
    }

    @Override
    public ValidationResponse validate(UUID gameId, UUID athleteId, UserDetails principal) {
        return service.validate(gameId, athleteId, principal.getUsername());
    }

    @Override
    public CheckInResponse setMatchNumber(UUID gameId, UUID athleteId, MatchNumberRequest request) {
        return service.setMatchNumber(gameId, athleteId, request);
    }

    @Override
    public List<CheckInResponse> getValidations(UUID gameId) {
        return service.getCheckinList(gameId);
    }
}
