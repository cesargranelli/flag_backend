package br.com.flagplatform.club.service;

import br.com.flagplatform.club.ClubInfo;
import br.com.flagplatform.club.ClubLookup;
import br.com.flagplatform.club.dto.request.CreateClubRequest;
import br.com.flagplatform.club.dto.request.UpdateClubRequest;
import br.com.flagplatform.club.dto.response.ClubResponse;
import br.com.flagplatform.club.entity.ClubEntity;
import br.com.flagplatform.club.exception.ClubNotFoundException;
import br.com.flagplatform.club.mapper.ClubMapper;
import br.com.flagplatform.club.repository.ClubRepository;
import br.com.flagplatform.common.enums.OrganizationStatus;
import br.com.flagplatform.common.pagination.PagedResponse;
import br.com.flagplatform.organization.OrganizationLookup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ClubService implements ClubLookup {

    private final ClubRepository repository;
    private final ClubMapper mapper;
    private final OrganizationLookup organizationLookup;

    @Transactional
    public ClubResponse create(UUID organizationId, CreateClubRequest request) {
        organizationLookup.assertExists(organizationId);

        ClubEntity entity = mapper.toEntity(request);
        entity.setOrganizationId(organizationId);
        entity.setStatus(OrganizationStatus.ACTIVE);

        return mapper.toResponse(repository.save(entity));
    }

    public PagedResponse<ClubResponse> findByOrganization(UUID organizationId, int page, int size) {
        organizationLookup.assertExists(organizationId);

        Page<ClubEntity> result = repository.findAllByOrganizationId(
                organizationId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name")));

        return new PagedResponse<>(
                mapper.toResponseList(result.getContent()),
                result.getTotalElements());
    }

    public ClubResponse findById(UUID id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Transactional
    public ClubResponse update(UUID id, UpdateClubRequest request) {
        ClubEntity entity = findEntityById(id);
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    private ClubEntity findEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ClubNotFoundException(id));
    }

    @Override
    public void assertExists(UUID id) {
        findEntityById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public ClubInfo findClubInfoById(UUID id) {
        ClubEntity entity = findEntityById(id);
        return new ClubInfo(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getName(),
                entity.getShortName(),
                entity.getSportName(),
                entity.getLogoUrl(),
                entity.getStatus()
        );
    }

    @Override
    public UUID findOrganizationIdByClubId(UUID id) {
        ClubEntity entity = findEntityById(id);
        return entity.getOrganizationId();
    }

}
