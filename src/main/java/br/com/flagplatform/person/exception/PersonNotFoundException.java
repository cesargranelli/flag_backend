package br.com.flagplatform.person.exception;

import java.util.UUID;

public class PersonNotFoundException extends RuntimeException {

    public PersonNotFoundException(UUID id) {
        super("Pessoa não encontrada: " + id);
    }

}
