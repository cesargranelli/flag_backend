package br.com.flagplatform.user.exception;

import br.com.flagplatform.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Lançada quando tenta-se definir custom claims para um usuário que ainda
 * não está vinculado ao Firebase Auth.
 */
public class FirebaseUserNotLinkedException extends ApiException {

    public FirebaseUserNotLinkedException(UUID userId) {
        super(
                HttpStatus.PRECONDITION_FAILED,
                "User not linked to Firebase",
                "User '%s' is not linked to Firebase Auth. "
                        + "Call POST /api/v1/admin/users/{id}/firebase-link first.".formatted(userId),
                "firebase_user_not_linked"
        );
    }

}