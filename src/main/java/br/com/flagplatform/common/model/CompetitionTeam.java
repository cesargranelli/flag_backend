package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Time inscrito em uma competição.
 * 
 * Mapeado para a subcollection 'competitionTeams' dentro de 'competitions'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionTeam {

    @DocumentId
    private String id;

    private String teamId;

    private String groupId;

    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, ACTIVE

    @ServerTimestamp
    private Instant enrolledAt;

    // Dados desnormalizados
    private String teamName;

    private String teamLogoUrl;
}
