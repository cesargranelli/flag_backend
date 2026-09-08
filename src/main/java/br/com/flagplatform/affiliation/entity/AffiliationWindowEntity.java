package br.com.flagplatform.affiliation.entity;

import br.com.flagplatform.common.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "affiliation_windows", schema = "platform")
public class AffiliationWindowEntity extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(nullable = false, length = 20)
    private String season;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 20)
    private String status; // OPEN, CLOSED

    @Column(length = 1000)
    private String instructions;

    @Column(name = "created_by_email", length = 150)
    private String createdByEmail;

    public boolean isOpen() {
        if (!"OPEN".equalsIgnoreCase(status)) return false;
        var today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }
}
