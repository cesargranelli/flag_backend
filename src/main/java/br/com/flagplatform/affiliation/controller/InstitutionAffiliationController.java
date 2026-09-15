package br.com.flagplatform.affiliation.controller;

import br.com.flagplatform.affiliation.dto.*;
import br.com.flagplatform.affiliation.service.InstitutionAffiliationService;
import br.com.flagplatform.common.enums.AffiliationStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InstitutionAffiliationController implements InstitutionAffiliationApi {

    private final InstitutionAffiliationService service;

    @Override
    public AffiliationResponse requestAffiliation(UUID institutionId, RequestAffiliationRequest req, Authentication authentication) {
        return service.requestAffiliation(institutionId, req, authentication.getName());
    }

    @Override
    public List<AffiliationResponse> listByInstitution(UUID institutionId) {
        return service.listByInstitution(institutionId);
    }

    @Override
    public List<AffiliationResponse> listByOrganization(UUID organizationId, String season, AffiliationStatus status) {
        return service.listByOrganization(organizationId, season, status);
    }

    @Override
    public AffiliationResponse approve(UUID organizationId, UUID affiliationId, Authentication authentication) {
        return service.approve(organizationId, affiliationId, authentication.getName());
    }

    @Override
    public AffiliationResponse reject(UUID organizationId, UUID affiliationId, RejectAffiliationRequest req, Authentication authentication) {
        return service.reject(organizationId, affiliationId, req, authentication.getName());
    }

    @Override
    public AffiliationWindowResponse openWindow(UUID organizationId, CreateAffiliationWindowRequest req, Authentication authentication) {
        return service.openWindow(organizationId, req, authentication.getName());
    }

    @Override
    public AffiliationWindowResponse closeWindow(UUID organizationId, String season) {
        return service.closeWindow(organizationId, season);
    }

    @Override
    public List<AffiliationWindowResponse> listWindows(UUID organizationId) {
        return service.listWindows(organizationId);
    }

    @Override
    public List<AffiliationWindowResponse> listOpenWindows() {
        return service.listOpenWindows();
    }
}
