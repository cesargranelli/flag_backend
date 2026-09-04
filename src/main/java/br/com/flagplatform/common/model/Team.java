package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Time de uma organização.
 * 
 * Mapeado para a collection 'teams' no Firestore.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @DocumentId
    private String id;

    private String organizationId;

    private String name;

    private String shortName;

    private String logoUrl;

    private String sport;

    private String divisionId;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, DISABLED

    @ServerTimestamp
    private Instant createdAt;

    @ServerTimestamp
    private Instant updatedAt;

    // Dados desnormalizados
    private String organizationName;

    private String organizationLogoUrl;
}
