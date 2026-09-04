package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Campeonato/competição.
 * 
 * Mapeado para a collection 'competitions' no Firestore.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Competition {

    @DocumentId
    private String id;

    private String seasonId;

    private String organizationId;

    private String name;

    private String sport;

    private String modality;

    private String gender; // MALE, FEMALE, MIXED

    private String ageGroup;

    private String groupingType; // SINGLE_ELIMINATION, ROUND_ROBIN, SWISS, LEAGUE, CUSTOM

    private String venueId;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private String status = "DRAFT"; // DRAFT, REGISTRATION_OPEN, IN_PROGRESS, FINISHED, CANCELLED

    private Map<String, Object> eligibilityRules; // allowedGenders, minAge, maxAge

    @ServerTimestamp
    private Instant createdAt;

    @ServerTimestamp
    private Instant updatedAt;

    // Dados desnormalizados
    private String organizationName;

    private String seasonName;

    private String venueName;
}
