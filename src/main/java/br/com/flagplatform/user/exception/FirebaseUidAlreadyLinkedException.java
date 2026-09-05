package br.com.flagplatform.user.exception;

import br.com.flagplatform.common.exception.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Lançada quando tenta vincular um Firebase UID que já está vinculado a outro usuário.
 */
public class FirebaseUidAlreadyLinkedException extends ApiException {

    public FirebaseUidAlreadyLinkedException(String firebaseUid, String existingUserEmail) {
        super(
                HttpStatus.CONFLICT,
                "Firebase UID already linked",
                "Firebase UID '%s' is already linked to user '%s'. "
                        + "Use a different Firebase UID or unlink the existing account.".formatted(
                        firebaseUid, existingUserEmail),
                "firebase_uid_already_linked"
        );
    }

}
