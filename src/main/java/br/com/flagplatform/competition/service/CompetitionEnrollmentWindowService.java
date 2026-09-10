package br.com.flagplatform.competition.service;

import br.com.flagplatform.competition.CompetitionLookup;
import br.com.flagplatform.competition.dto.request.CreateCompetitionEnrollmentWindowRequest;
import br.com.flagplatform.competition.dto.response.CompetitionEnrollmentWindowResponse;
import br.com.flagplatform.competition.entity.CompetitionEnrollmentWindowEntity;
import br.com.flagplatform.competition.repository.CompetitionEnrollmentWindowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CompetitionEnrollmentWindowService {

    private final CompetitionEnrollmentWindowRepository windowRepository;
    private final CompetitionLookup competitionLookup;

    @Transactional
    public CompetitionEnrollmentWindowResponse openWindow(
            UUID competitionId,
            CreateCompetitionEnrollmentWindowRequest req,
            String userEmail) {
        competitionLookup.assertExists(competitionId);

        if (req.endDate().isBefore(req.startDate())) {
            throw new IllegalArgumentException("A data final não pode ser anterior à data de início");
        }

        var entity = windowRepository.findByCompetitionId(competitionId)
                .orElseGet(() -> {
                    var w = new CompetitionEnrollmentWindowEntity();
                    w.setCompetitionId(competitionId);
                    return w;
                });

        entity.setTitle(req.title());
        entity.setStartDate(req.startDate());
        entity.setEndDate(req.endDate());
        entity.setStatus("OPEN");
        entity.setInstructions(req.instructions());
        entity.setCreatedByEmail(userEmail);

        var saved = windowRepository.save(entity);
        var competitionInfo = competitionLookup.findCompetitionInfoById(competitionId);
        return toResponse(saved, competitionInfo.name());
    }

    @Transactional
    public CompetitionEnrollmentWindowResponse closeWindow(UUID competitionId) {
        competitionLookup.assertExists(competitionId);

        var entity = windowRepository.findByCompetitionId(competitionId)
                .orElseThrow(() -> new EntityNotFoundException("Janela de inscrição não encontrada para esta competição"));

        entity.setStatus("CLOSED");
        var saved = windowRepository.save(entity);
        var competitionInfo = competitionLookup.findCompetitionInfoById(competitionId);
        return toResponse(saved, competitionInfo.name());
    }

    @Transactional(readOnly = true)
    public CompetitionEnrollmentWindowResponse findByCompetitionId(UUID competitionId) {
        competitionLookup.assertExists(competitionId);

        var entity = windowRepository.findByCompetitionId(competitionId)
                .orElseThrow(() -> new EntityNotFoundException("Janela de inscrição não encontrada para esta competição"));

        var competitionInfo = competitionLookup.findCompetitionInfoById(competitionId);
        return toResponse(entity, competitionInfo.name());
    }

    @Transactional(readOnly = true)
    public List<CompetitionEnrollmentWindowResponse> listOpenWindows() {
        return windowRepository.findAllByStatus("OPEN").stream()
                .filter(CompetitionEnrollmentWindowEntity::isOpen)
                .map(w -> {
                    var info = competitionLookup.findCompetitionInfoById(w.getCompetitionId());
                    return toResponse(w, info != null ? info.name() : "Competição");
                })
                .toList();
    }

    /**
     * Verifica se a janela de inscrição de uma competição está aberta.
     * Usado pelo TeamService para validar inscrição de equipes.
     */
    public boolean isWindowOpen(UUID competitionId) {
        return windowRepository.findByCompetitionId(competitionId)
                .filter(CompetitionEnrollmentWindowEntity::isOpen)
                .isPresent();
    }

    private CompetitionEnrollmentWindowResponse toResponse(
            CompetitionEnrollmentWindowEntity entity,
            String competitionName) {
        return new CompetitionEnrollmentWindowResponse(
                entity.getId(),
                entity.getCompetitionId(),
                competitionName,
                entity.getTitle(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus(),
                entity.isOpen(),
                entity.getInstructions(),
                entity.getCreatedAt(),
                entity.getCreatedByEmail()
        );
    }
}
