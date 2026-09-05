package br.com.flagplatform.user.dto.response;

/**
 * Resposta do endpoint de vinculação de conta Firebase.
 *
 * @param firebaseUid UID do Firebase Auth vinculado/criado
 * @param userId     ID do usuário no PostgreSQL
 * @param email      E-mail do usuário
 */
public record FirebaseLinkResponse(
        String firebaseUid,
        String userId,
        String email
) {
}
