package br.com.flagplatform.user.exception;

import br.com.flagplatform.common.exception.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Lançada quando tenta vincular um usuário que já possui UID do Firebase
 * diferente do que está sendo vinculado.
 */
public class FirebaseUidConflictException extends ApiException {

    public FirebaseUidConflictException(String existingUid, String requestedUid) {
        super(
                HttpStatus.CONFLICT,
                "Firebase UID conflict",
                "User is already linked to Firebase UID '%s'. "
                        + "Unlink the existing account before linking to a new one.".formatted(existingUid),
                "firebase_uid_conflict"
        );
    }

}
