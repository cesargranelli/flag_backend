package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Usuário do sistema.
 * 
 * Mapeado para a collection 'users' no Firestore.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @DocumentId
    private String id;

    private String personId;

    private String email;

    private String passwordHash;

    @Builder.Default
    private String role = "USER"; // SUPER_ADMIN, ADMIN, ORGANIZER, REFEREE, USER

    @Builder.Default
    private String status = "PENDING"; // PENDING, ACTIVE, REJECTED

    @ServerTimestamp
    private Instant createdAt;

    @ServerTimestamp
    private Instant updatedAt;
}
