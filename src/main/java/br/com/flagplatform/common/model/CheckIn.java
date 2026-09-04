package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Check-in de atleta em um jogo.
 * 
 * Mapeado para a subcollection 'checkins' dentro de 'games'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckIn {

    @DocumentId
    private String id;

    private String personId;

    private String teamId;

    @ServerTimestamp
    private Instant checkedInAt;

    @Builder.Default
    private String status = "CHECKED_IN"; // PENDING, CHECKED_IN, ABSENT

    private Integer matchNumber;

    // Dados desnormalizados
    private String personName;

    private String personPhotoUrl;

    private String teamName;
}
