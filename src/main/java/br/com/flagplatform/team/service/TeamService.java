package br.com.flagplatform.team.service;

import br.com.flagplatform.common.enums.CompetitionTeamStatus;
import br.com.flagplatform.common.enums.OrganizationStatus;
import br.com.flagplatform.competition.CompetitionLookup;
import br.com.flagplatform.institution.InstitutionLookup;
import br.com.flagplatform.team.TeamInfo;
import br.com.flagplatform.team.TeamLookup;
import br.com.flagplatform.team.dto.request.CreateTeamRequest;
import br.com.flagplatform.team.dto.request.EnrollTeamRequest;
import br.com.flagplatform.team.dto.request.UpdateCompetitionTeamRequest;
import br.com.flagplatform.team.dto.request.UpdateTeamRequest;
import br.com.flagplatform.team.dto.response.CompetitionTeamResponse;
import br.com.flagplatform.team.dto.response.TeamResponse;
import br.com.flagplatform.team.entity.CompetitionTeamEntity;
import br.com.flagplatform.team.entity.TeamEntity;
import br.com.flagplatform.team.exception.DuplicateTeamNameException;
import br.com.flagplatform.team.exception.TeamNotFoundException;
import br.com.flagplatform.team.mapper.TeamMapper;
import br.com.flagplatform.team.repository.CompetitionTeamRepository;
import br.com.flagplatform.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TeamService implements TeamLookup {

    private final TeamMapper mapper;
    private final TeamRepository teamRepository;
    private final CompetitionTeamRepository competitionTeamRepository;
    private final InstitutionLookup institutionLookup;
    private final CompetitionLookup competitionLookup;

    @Transactional
    public TeamResponse create(UUID clubId, CreateTeamRequest request, String currentUserEmail) {
        institutionLookup.assertExists(clubId);

        if (teamRepository.existsByClubIdAndNameIgnoreCase(
                clubId, request.name())) {
            throw new DuplicateTeamNameException(request.name());
        }

        TeamEntity entity = mapper.toEntity(request);
        entity.setClubId(clubId);
        entity.setStatus(OrganizationStatus.ACTIVE);

        return toResponse(teamRepository.save(entity));
    }

    @Transactional
    public TeamResponse createForInstitution(UUID institutionId, CreateTeamRequest request, String currentUserEmail) {
        institutionLookup.assertExists(institutionId);

        if (teamRepository.existsByClubIdAndNameIgnoreCase(
                institutionId, request.name())) {
            throw new DuplicateTeamNameException(request.name());
        }

        TeamEntity entity = mapper.toEntity(request);
        entity.setClubId(institutionId);
        entity.setStatus(OrganizationStatus.ACTIVE);

        return toResponse(teamRepository.save(entity));
    }

    public List<TeamResponse> findByOrganizationId(UUID organizationId) {
        return toResponseList(teamRepository.findAllByClubIdOrderByNameAsc(organizationId));
    }

    public List<TeamResponse> findByInstitutionId(UUID institutionId) {
        return toResponseList(teamRepository.findAllByClubIdOrderByNameAsc(institutionId));
    }

    public TeamResponse findById(UUID id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public TeamResponse update(UUID id, UpdateTeamRequest request, String currentUserEmail) {
        TeamEntity entity = findEntityById(id);

        if (teamRepository.existsByClubIdAndNameIgnoreCaseAndIdNot(
                request.clubId(), request.name(), id)) {
            throw new DuplicateTeamNameException(request.name());
        }

        mapper.updateEntity(entity, request);

        return toResponse(teamRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id, String currentUserEmail) {
        TeamEntity entity = findEntityById(id);
        teamRepository.delete(entity);
    }

    @Transactional
    public void deactivate(UUID id, String currentUserEmail) {
        TeamEntity entity = findEntityById(id);
        entity.setStatus(OrganizationStatus.INACTIVE);
        teamRepository.save(entity);
    }

    @Transactional
    public void reactivate(UUID id, String currentUserEmail) {
        TeamEntity entity = findEntityById(id);
        entity.setStatus(OrganizationStatus.ACTIVE);
        teamRepository.save(entity);
    }

    // --- CompetitionTeam endpoints ---

    @Transactional
    public CompetitionTeamResponse enrollInCompetition(
            UUID competitionId, UUID teamId, EnrollTeamRequest request, String currentUserEmail) {
        competitionLookup.assertExists(competitionId);

        if (!competitionLookup.isEnrollmentWindowOpen(competitionId)) {
            throw new IllegalStateException(
                    "A janela de inscrição de equipes para esta competição está encerrada ou não foi aberta.");
        }

        TeamEntity team = findEntityById(teamId);

        boolean isCompetitionManager = true;
        try {
            competitionLookup.assertManagedBy(competitionId, currentUserEmail);
        } catch (Exception e) {
            isCompetitionManager = false;
        }

        if (competitionTeamRepository.existsByCompetitionIdAndTeamId(competitionId, teamId)) {
            throw new IllegalArgumentException("Time já inscrito nesta competição");
        }

        CompetitionTeamEntity entity = new CompetitionTeamEntity();
        entity.setCompetitionId(competitionId);
        entity.setTeamId(teamId);

        if (isCompetitionManager && request != null) {
            entity.setStatus(request.status() != null ? request.status() : CompetitionTeamStatus.PENDING);
            entity.setGroupName(request.groupName());
            entity.setConferenceName(request.conferenceName());
            entity.setDivisionName(request.divisionName());
            entity.setSeedNumber(request.seedNumber());
        } else {
            entity.setStatus(CompetitionTeamStatus.PENDING);
        }

        CompetitionTeamEntity saved = competitionTeamRepository.save(entity);
        return toCompetitionTeamResponse(saved, team);
    }

    @Transactional
    public CompetitionTeamResponse updateAllocation(
            UUID competitionId, UUID teamId, UpdateCompetitionTeamRequest request, String currentUserEmail) {
        competitionLookup.assertExists(competitionId);
        competitionLookup.assertManagedBy(competitionId, currentUserEmail);
        TeamEntity team = findEntityById(teamId);

        CompetitionTeamEntity entity = competitionTeamRepository
                .findByCompetitionIdAndTeamId(competitionId, teamId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Inscrição do time " + teamId + " na competição " + competitionId + " não encontrada"));

        if (request.status() != null) {
            entity.setStatus(request.status());
        }
        entity.setGroupName(request.groupName());
        entity.setConferenceName(request.conferenceName());
        entity.setDivisionName(request.divisionName());
        entity.setSeedNumber(request.seedNumber());

        CompetitionTeamEntity saved = competitionTeamRepository.save(entity);
        return toCompetitionTeamResponse(saved, team);
    }

    @Transactional
    public CompetitionTeamResponse approveTeam(UUID competitionId, UUID teamId, String currentUserEmail) {
        competitionLookup.assertExists(competitionId);
        competitionLookup.assertManagedBy(competitionId, currentUserEmail);
        TeamEntity team = findEntityById(teamId);

        CompetitionTeamEntity entity = competitionTeamRepository
                .findByCompetitionIdAndTeamId(competitionId, teamId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Inscrição do time " + teamId + " na competição " + competitionId + " não encontrada"));

        entity.setStatus(CompetitionTeamStatus.APPROVED);
        CompetitionTeamEntity saved = competitionTeamRepository.save(entity);
        return toCompetitionTeamResponse(saved, team);
    }

    @Transactional
    public CompetitionTeamResponse rejectTeam(UUID competitionId, UUID teamId, String currentUserEmail) {
        competitionLookup.assertExists(competitionId);
        competitionLookup.assertManagedBy(competitionId, currentUserEmail);
        TeamEntity team = findEntityById(teamId);

        CompetitionTeamEntity entity = competitionTeamRepository
                .findByCompetitionIdAndTeamId(competitionId, teamId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Inscrição do time " + teamId + " na competição " + competitionId + " não encontrada"));

        entity.setStatus(CompetitionTeamStatus.REJECTED);
        CompetitionTeamEntity saved = competitionTeamRepository.save(entity);
        return toCompetitionTeamResponse(saved, team);
    }

    @Transactional
    public void removeFromCompetition(UUID competitionId, UUID teamId, String currentUserEmail) {
        competitionLookup.assertExists(competitionId);
        competitionLookup.assertManagedBy(competitionId, currentUserEmail);

        CompetitionTeamEntity entity = competitionTeamRepository
                .findByCompetitionIdAndTeamId(competitionId, teamId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Inscrição do time " + teamId + " na competição " + competitionId + " não encontrada"));
        competitionTeamRepository.delete(entity);
    }

    public List<CompetitionTeamResponse> findByCompetitionId(UUID competitionId) {
        return competitionTeamRepository.findAllByCompetitionIdOrderByCreatedAtAsc(competitionId)
                .stream()
                .map(this::toCompetitionTeamResponse)
                .toList();
    }

    public List<CompetitionTeamResponse> findByTeamId(UUID teamId) {
        return competitionTeamRepository.findAllByTeamIdOrderByCreatedAtAsc(teamId)
                .stream()
                .map(this::toCompetitionTeamResponse)
                .toList();
    }

    public List<TeamResponse> findAll() {
        return toResponseList(teamRepository.findAll());
    }

    private CompetitionTeamResponse toCompetitionTeamResponse(CompetitionTeamEntity ct) {
        TeamEntity team = teamRepository.findById(ct.getTeamId()).orElse(null);
        return toCompetitionTeamResponse(ct, team);
    }

    private CompetitionTeamResponse toCompetitionTeamResponse(CompetitionTeamEntity ct, TeamEntity team) {
        String teamName = team != null ? team.getName() : "Desconhecido";
        String teamShortName = team != null ? team.getShortName() : null;
        String teamLogoUrl = team != null ? team.getLogoUrl() : null;
        UUID clubId = team != null ? team.getClubId() : null;
        String clubName = clubId != null
                ? institutionLookup.findTradeNameById(clubId)
                : null;
        return new CompetitionTeamResponse(
                ct.getId(),
                ct.getCompetitionId(),
                ct.getTeamId(),
                teamName,
                teamShortName,
                teamLogoUrl,
                null,
                clubName,
                clubId,
                clubName,
                ct.getStatus(),
                ct.getGroupName(),
                ct.getConferenceName(),
                ct.getDivisionName(),
                ct.getSeedNumber(),
                ct.getCreatedAt(),
                ct.getUpdatedAt());
    }

    private TeamResponse toResponse(TeamEntity entity) {
        TeamResponse base = mapper.toResponse(entity);
        String clubName = entity.getClubId() != null
                ? institutionLookup.findTradeNameById(entity.getClubId())
                : null;
        return new TeamResponse(
                base.id(),
                null,
                null,
                entity.getClubId(),
                clubName,
                base.name(),
                base.shortName(),
                base.sportName(),
                base.logoUrl(),
                base.status(),
                base.createdAt(),
                base.updatedAt());
    }

    private List<TeamResponse> toResponseList(List<TeamEntity> entities) {
        return entities.stream().map(this::toResponse).toList();
    }

    private TeamEntity findEntityById(UUID id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));
    }

    @Override
    public void assertExists(UUID id) {
        findEntityById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return teamRepository.existsById(id);
    }

    @Override
    public List<TeamInfo> findTeamInfoByOrganizationId(UUID organizationId) {
        return teamRepository.findAllByClubIdOrderByNameAsc(organizationId).stream()
                .map(team -> new TeamInfo(team.getId(), team.getName()))
                .toList();
    }

    @Override
    public TeamInfo findTeamInfoById(UUID id) {
        TeamEntity entity = findEntityById(id);
        return new TeamInfo(entity.getId(), entity.getName());
    }

}
