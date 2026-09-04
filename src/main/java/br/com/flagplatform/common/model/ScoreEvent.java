package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Evento de placar em um jogo.
 * 
 * Mapeado para a subcollection 'scoreEvents' dentro de 'games'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreEvent {

    @DocumentId
    private String id;

    private String personId;

    private String type; // TOUCHDOWN, FIELD_GOAL, SAFETY, EXTRA_POINT, CONVERSION

    private String teamId;

    private Integer quarter;

    private Instant timestamp;

    // Dados desnormalizados
    private String personName;

    private String teamName;
}
