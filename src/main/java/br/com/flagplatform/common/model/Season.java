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
 * Temporada de uma organização.
 * 
 * Mapeado para a collection 'seasons' no Firestore.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Season {

    @DocumentId
    private String id;

    private String organizationId;

    private String name;

    private String sport;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private String status = "DRAFT"; // DRAFT, IN_PROGRESS, CLOSED

    @ServerTimestamp
    private Instant createdAt;

    @ServerTimestamp
    private Instant updatedAt;
}
