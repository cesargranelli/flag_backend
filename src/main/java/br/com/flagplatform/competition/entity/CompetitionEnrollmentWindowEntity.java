package br.com.flagplatform.competition.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Janela de inscricao de equipes em uma competicao.
 *
 * <p>Analogamente a {@code AffiliationWindowEntity}, a organizacao promotora
 * abre e encerra o periodo. Enquanto a janela estiver OPEN e a data de hoje
 * estiver entre startDate e endDate, a agremiacao pode inscrever suas equipes.
 */
@Getter
@Setter
@Entity
@Table(name = "competition_enrollment_windows", schema = "platform")
public class CompetitionEnrollmentWindowEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "competition_id", nullable = false)
    private UUID competitionId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /** OPEN ou CLOSED. */
    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 1000)
    private String instructions;

    @Column(name = "created_by_email", length = 150)
    private String createdByEmail;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Retorna {@code true} se o status e OPEN e a data atual esta dentro
     * do intervalo [startDate, endDate] (inclusive).
     */
    public boolean isOpen() {
        if (!"OPEN".equalsIgnoreCase(status)) return false;
        var today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }
}
