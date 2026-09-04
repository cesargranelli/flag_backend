package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Jogo de uma competição.
 * 
 * Mapeado para a collection 'games' no Firestore.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @DocumentId
    private String id;

    private String competitionId;

    private String roundId;

    private String venueId;

    private String homeTeamId;

    private String awayTeamId;

    private Instant scheduledAt;

    private Instant actualStartTime;

    private Instant actualEndTime;

    private Integer homeScore;

    private Integer awayScore;

    @Builder.Default
    private String status = "SCHEDULED"; // SCHEDULED, OPENING, IN_PROGRESS, CONFERENCE, FINISHED, CANCELLED

    private String notes;

    @ServerTimestamp
    private Instant createdAt;

    @ServerTimestamp
    private Instant updatedAt;

    // Dados desnormalizados
    private String competitionName;

    private Integer roundNumber;

    private String roundName;

    private String homeTeamName;

    private String homeTeamLogoUrl;

    private String awayTeamName;

    private String awayTeamLogoUrl;

    private String venueName;

    private String venueAddress;
}
