package br.com.flagplatform.competition.repository;

import br.com.flagplatform.competition.entity.CompetitionEnrollmentWindowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompetitionEnrollmentWindowRepository extends JpaRepository<CompetitionEnrollmentWindowEntity, UUID> {

    Optional<CompetitionEnrollmentWindowEntity> findByCompetitionId(UUID competitionId);

    List<CompetitionEnrollmentWindowEntity> findAllByStatus(String status);
}
