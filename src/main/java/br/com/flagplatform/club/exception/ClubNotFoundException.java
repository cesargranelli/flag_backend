package br.com.flagplatform.club.exception;

import br.com.flagplatform.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ClubNotFoundException extends ApiException {

    public ClubNotFoundException(UUID id) {
        super(
                HttpStatus.NOT_FOUND,
                "Club not found",
                "Club with id '%s' was not found.".formatted(id),
                "club_not_found"
        );
    }

}
