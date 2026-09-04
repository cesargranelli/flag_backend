package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Rodada dentro de uma competição.
 * 
 * Mapeado para a subcollection 'rounds' dentro de 'competitions'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Round {

    @DocumentId
    private String id;

    private int number;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    @ServerTimestamp
    private Instant createdAt;
}
