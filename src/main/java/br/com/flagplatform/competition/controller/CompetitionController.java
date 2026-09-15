package br.com.flagplatform.competition.controller;

import br.com.flagplatform.common.security.CurrentUser;
import br.com.flagplatform.competition.dto.request.CreateCompetitionRequest;
import br.com.flagplatform.competition.dto.request.UpdateCompetitionRequest;
import br.com.flagplatform.competition.dto.response.CompetitionResponse;
import br.com.flagplatform.competition.dto.response.CompetitionSummaryResponse;
import br.com.flagplatform.competition.service.CompetitionService;
import jakarta.servlet.http.HttpServletResponse;
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
public class CompetitionController implements CompetitionApi {

    private final CompetitionService service;

    @Override
    public CompetitionResponse create(CreateCompetitionRequest request, Authentication authentication) {
        log.info("Recebida requisicao POST /api/v1/competitions de {}: {}", authentication.getName(), request);
        return service.create(request, authentication.getName());
    }

    @Override
    public List<CompetitionSummaryResponse> listAll(int page, int size, boolean includeDisabled, Authentication authentication, HttpServletResponse response) {
        var result = service.listAllPublic(page, size, includeDisabled, CurrentUser.isAdmin(authentication));
        response.setHeader("X-Total-Count", String.valueOf(result.total()));
        return result.items();
    }

    @Override
    public CompetitionResponse getById(UUID id, Authentication authentication) {
        return service.findById(id, CurrentUser.isAdmin(authentication));
    }

    @Override
    public void deactivate(UUID id, Authentication authentication) {
        service.deactivate(id, authentication.getName());
    }

    @Override
    public void reactivate(UUID id) {
        service.reactivate(id);
    }

    @Override
    public void finish(UUID id, Authentication authentication) {
        service.finish(id, authentication.getName());
    }

    @Override
    public List<CompetitionResponse> findByOrganizationId(UUID organizationId, boolean includeDisabled, Authentication authentication) {
        return service.findByOrganizationId(organizationId, includeDisabled, CurrentUser.isAdmin(authentication));
    }

    @Override
    public CompetitionResponse update(UUID id, UpdateCompetitionRequest request, Authentication authentication) {
        log.info("Recebida requisicao PUT /api/v1/competitions/{} de {}: {}", id, authentication.getName(), request);
        return service.update(id, request, authentication.getName());
    }
}
