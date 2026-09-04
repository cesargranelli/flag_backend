package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Entrada de elenco (atleta em um CompetitionTeam).
 * 
 * Mapeado para a subcollection 'roster' dentro de 'competitions'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RosterEntry {

    @DocumentId
    private String id;

    private String competitionTeamId;

    private String personId;

    @Builder.Default
    private String role = "PLAYER"; // PLAYER, COACH, MANAGER

    private String jerseyNumber;

    private String nickname;

    @Builder.Default
    private String eligibilityStatus = "PENDING"; // PENDING, APPROVED, REJECTED

    private String eligibilityReason;

    @Builder.Default
    private String status = "ACTIVE"; // PENDING, APPROVED, ACTIVE, INACTIVE

    @ServerTimestamp
    private Instant enrolledAt;

    // Dados desnormalizados
    private String personName;

    private String personPhotoUrl;

    private String teamName;
}
