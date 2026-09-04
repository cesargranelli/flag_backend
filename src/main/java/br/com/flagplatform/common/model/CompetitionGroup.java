package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Grupo dentro de uma competição (conferência, divisão, pool).
 * 
 * Mapeado para a subcollection 'groups' dentro de 'competitions'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionGroup {

    @DocumentId
    private String id;

    private String name;

    private String type; // CONFERENCE, DIVISION, POOL, BRACKET

    private int sortOrder;
}
