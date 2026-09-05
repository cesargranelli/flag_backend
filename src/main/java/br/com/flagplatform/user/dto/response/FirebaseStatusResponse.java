package br.com.flagplatform.user.dto.response;

import java.util.List;
import java.util.UUID;

/**
 * Resposta com o status de vinculação Firebase de um usuário.
 *
 * @param userId              ID do usuário no PostgreSQL
 * @param email               E-mail do usuário
 * @param linked              Se o usuário possui UID do Firebase vinculado
 * @param firebaseUid         UID do Firebase (null se não vinculado)
 * @param currentClaims       Claims atualmente definidas no Firebase (null se não vinculado)
 * @param claimsSyncedWithDb  Se as claims do Firebase estão sincronizadas com o PostgreSQL
 */
public record FirebaseStatusResponse(
        UUID userId,
        String email,
        boolean linked,
        String firebaseUid,
        FirebaseClaimsDto currentClaims,
        boolean claimsSyncedWithDb
) {

    /**
     * Resumo das Custom Claims atualmente definidas no Firebase.
     */
    public record FirebaseClaimsDto(
            String role,
            String organizationId,
            String clubId,
            List<String> skills
    ) {
    }
}
