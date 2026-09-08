package br.com.flagplatform.institution.exception;

import br.com.flagplatform.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class InstitutionNotFoundException extends ApiException {

    public InstitutionNotFoundException(UUID id) {
        super(
                HttpStatus.NOT_FOUND,
                "Institution not found",
                "Institution with id '%s' was not found.".formatted(id),
                "institution_not_found"
        );
    }
}