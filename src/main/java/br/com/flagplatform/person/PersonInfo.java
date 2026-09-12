package br.com.flagplatform.person;

import java.util.UUID;

/**
 * Projeção pública de uma pessoa para outros módulos (ex: elenco de times).
 */
public record PersonInfo(
        UUID id,
        String name,
        String photoUrl
) {
}
