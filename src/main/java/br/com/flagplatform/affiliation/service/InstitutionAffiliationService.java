package br.com.flagplatform.affiliation.service;

import br.com.flagplatform.affiliation.dto.AffiliationResponse;
import br.com.flagplatform.affiliation.dto.RejectAffiliationRequest;
import br.com.flagplatform.affiliation.dto.RequestAffiliationRequest;
import br.com.flagplatform.affiliation.entity.InstitutionAffiliationEntity;
import br.com.flagplatform.affiliation.repository.InstitutionAffiliationRepository;
import br.com.flagplatform.common.enums.AffiliationStatus;
import br.com.flagplatform.institution.entity.InstitutionEntity;
import br.com.flagplatform.institution.repository.InstitutionOrganizationRepository;
import br.com.flagplatform.institution.repository.InstitutionRepository;
import br.com.flagplatform.organization.entity.OrganizationEntity;
import br.com.flagplatform.organization.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstitutionAffiliationService {

    private final InstitutionAffiliationRepository affiliationRepository;
    private final InstitutionRepository institutionRepository;
    private final OrganizationRepository organizationRepository;
    private final InstitutionOrganizationRepository institutionOrganizationRepository;

    @Transactional
    public AffiliationResponse requestAffiliation(UUID institutionId, RequestAffiliationRequest req, String userEmail) {
        var institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new EntityNotFoundException("Agremiação não encontrada"));
        var organization = organizationRepository.findById(req.organizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organização não encontrada"));

        var existing = affiliationRepository.findByInstitutionIdAndOrganizationIdAndSeason(
                institutionId, req.organizationId(), req.season());

        if (existing.isPresent()) {
            var affil = existing.get();
            if (affil.getStatus() == AffiliationStatus.APPROVED) {
                throw new IllegalStateException("Esta agremiação já está filiada a esta organização na temporada " + req.season());
            }
            if (affil.getStatus() == AffiliationStatus.PENDING) {
                throw new IllegalStateException("Já existe uma solicitação de filiação pendente para esta temporada");
            }
            // Se foi rejeitado ou cancelado anteriormente, reabre como PENDING
            affil.setStatus(AffiliationStatus.PENDING);
            affil.setRequestedAt(LocalDateTime.now());
            affil.setRequestedBy(userEmail);
            affil.setReviewedAt(null);
            affil.setReviewedBy(null);
            affil.setRejectionReason(null);
            return toResponse(affiliationRepository.save(affil), institution, organization);
        }

        var entity = new InstitutionAffiliationEntity();
        entity.setInstitutionId(institutionId);
        entity.setOrganizationId(req.organizationId());
        entity.setSeason(req.season());
        entity.setStatus(AffiliationStatus.PENDING);
        entity.setRequestedAt(LocalDateTime.now());
        entity.setRequestedBy(userEmail);

        return toResponse(affiliationRepository.save(entity), institution, organization);
    }

    @Transactional(readOnly = true)
    public List<AffiliationResponse> listByInstitution(UUID institutionId) {
        var institution = institutionRepository.findById(institutionId)
                .orElseThrow(() -> new EntityNotFoundException("Agremiação não encontrada"));

        return affiliationRepository.findAllByInstitutionIdOrderBySeasonDescRequestedAtDesc(institutionId).stream()
                .map(a -> {
                    var org = organizationRepository.findById(a.getOrganizationId()).orElse(null);
                    return toResponse(a, institution, org);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AffiliationResponse> listByOrganization(UUID organizationId, String season, AffiliationStatus status) {
        var organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organização não encontrada"));

        List<InstitutionAffiliationEntity> list;
        if (season != null && !season.isBlank() && status != null) {
            list = affiliationRepository.findAllByOrganizationIdAndSeasonAndStatusOrderByRequestedAtDesc(organizationId, season, status);
        } else if (season != null && !season.isBlank()) {
            list = affiliationRepository.findAllByOrganizationIdAndSeasonOrderByRequestedAtDesc(organizationId, season);
        } else if (status != null) {
            list = affiliationRepository.findAllByOrganizationIdAndStatusOrderBySeasonDescRequestedAtDesc(organizationId, status);
        } else {
            list = affiliationRepository.findAllByOrganizationIdOrderBySeasonDescRequestedAtDesc(organizationId);
        }

        return list.stream()
                .map(a -> {
                    var inst = institutionRepository.findById(a.getInstitutionId()).orElse(null);
                    return toResponse(a, inst, organization);
                })
                .toList();
    }

    @Transactional
    public AffiliationResponse approve(UUID organizationId, UUID affiliationId, String userEmail) {
        var affil = affiliationRepository.findById(affiliationId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de filiação não encontrada"));

        if (!affil.getOrganizationId().equals(organizationId)) {
            throw new IllegalArgumentException("A filiação informada não pertence a esta organização");
        }

        affil.setStatus(AffiliationStatus.APPROVED);
        affil.setReviewedAt(LocalDateTime.now());
        affil.setReviewedBy(userEmail);
        affil.setRejectionReason(null);
        var saved = affiliationRepository.save(affil);

        // Sincroniza com a tabela relacional rápida institution_organizations caso ainda não esteja
        var orgIds = institutionOrganizationRepository.findOrganizationIds(affil.getInstitutionId());
        if (!orgIds.contains(organizationId)) {
            var updated = new java.util.ArrayList<>(orgIds);
            updated.add(organizationId);
            institutionOrganizationRepository.setOrganizations(affil.getInstitutionId(), updated);
        }

        var inst = institutionRepository.findById(affil.getInstitutionId()).orElse(null);
        var org = organizationRepository.findById(organizationId).orElse(null);
        return toResponse(saved, inst, org);
    }

    @Transactional
    public AffiliationResponse reject(UUID organizationId, UUID affiliationId, RejectAffiliationRequest req, String userEmail) {
        var affil = affiliationRepository.findById(affiliationId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de filiação não encontrada"));

        if (!affil.getOrganizationId().equals(organizationId)) {
            throw new IllegalArgumentException("A filiação informada não pertence a esta organização");
        }

        affil.setStatus(AffiliationStatus.REJECTED);
        affil.setReviewedAt(LocalDateTime.now());
        affil.setReviewedBy(userEmail);
        affil.setRejectionReason(req.reason());
        var saved = affiliationRepository.save(affil);

        var inst = institutionRepository.findById(affil.getInstitutionId()).orElse(null);
        var org = organizationRepository.findById(organizationId).orElse(null);
        return toResponse(saved, inst, org);
    }

    private AffiliationResponse toResponse(InstitutionAffiliationEntity entity, InstitutionEntity inst, OrganizationEntity org) {
        return new AffiliationResponse(
                entity.getId(),
                entity.getInstitutionId(),
                inst != null ? (inst.getTradeName() != null ? inst.getTradeName() : inst.getName()) : "Desconhecido",
                inst != null && inst.getType() != null ? inst.getType().name() : null,
                inst != null ? inst.getLogoUrl() : null,
                entity.getOrganizationId(),
                org != null ? org.getTradeName() : "Desconhecido",
                org != null && org.getOrganizationType() != null ? org.getOrganizationType().name() : null,
                entity.getSeason(),
                entity.getStatus(),
                entity.getRejectionReason(),
                entity.getRequestedAt(),
                entity.getRequestedBy(),
                entity.getReviewedAt(),
                entity.getReviewedBy()
        );
    }
}
