package br.com.flagplatform.user.exception;

import br.com.flagplatform.common.exception.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Lançada quando uma operação Firebase falha (ex: erro na criação de usuário,
 * definição de custom claims, etc.).
 */
public class FirebaseOperationException extends ApiException {

    public FirebaseOperationException(String operation, String detail) {
        super(
                HttpStatus.BAD_GATEWAY,
                "Firebase operation failed",
                "Failed to %s: %s".formatted(operation, detail),
                "firebase_operation_failed"
        );
    }

}
