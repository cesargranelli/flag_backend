package br.com.flagplatform.organization.controller;

import br.com.flagplatform.common.security.CurrentUser;
import br.com.flagplatform.organization.dto.request.AssociateClubRequest;
import br.com.flagplatform.organization.dto.request.CreateOrganizationRequest;
import br.com.flagplatform.organization.dto.request.UpdateOrganizationRequest;
import br.com.flagplatform.organization.dto.response.OrganizationCreatedResponse;
import br.com.flagplatform.organization.dto.response.OrganizationResponse;
import br.com.flagplatform.organization.service.OrganizationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrganizationController implements OrganizationApi {

    private final OrganizationService service;

    @Override
    public OrganizationCreatedResponse create(CreateOrganizationRequest request) {
        return service.create(request);
    }

    @Override
    public List<OrganizationResponse> list(int page, int size, boolean includeDisabled, Authentication authentication, HttpServletResponse response) {
        var result = service.findAll(page, size, includeDisabled, CurrentUser.isAdmin(authentication));
        response.setHeader("X-Total-Count", String.valueOf(result.total()));
        return result.items();
    }

    @Override
    public OrganizationResponse getById(UUID id, Authentication authentication) {
        return service.findById(id, CurrentUser.isAdmin(authentication));
    }

    @Override
    public OrganizationResponse update(UUID id, UpdateOrganizationRequest request) {
        return service.update(id, request);
    }

    @Override
    public void deactivate(UUID id) {
        service.deactivate(id);
    }

    @Override
    public void reactivate(UUID id) {
        service.reactivate(id);
    }

    @Override
    public List<OrganizationResponse> listClubs(UUID id) {
        return service.findClubs(id);
    }

    @Override
    public OrganizationResponse associateClub(UUID id, AssociateClubRequest request) {
        return service.associateClub(id, request.organizationId());
    }

    @Override
    public void removeClub(UUID id, UUID clubId) {
        service.removeClubAssociation(id, clubId);
    }
}
