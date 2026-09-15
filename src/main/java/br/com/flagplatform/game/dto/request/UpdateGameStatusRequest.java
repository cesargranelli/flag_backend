package br.com.flagplatform.game.dto.request;

import br.com.flagplatform.common.enums.GameStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateGameStatusRequest(
        @NotNull
        GameStatus status,

        /**
         * Nova data/hora do jogo. Obrigatório ao adiar (POSTPONED).
         * Opcional ao reagendar (SCHEDULED → SCHEDULED via POSTPONED → SCHEDULED).
         */
        LocalDateTime scheduledAt,

        /**
         * Novo campo do jogo. Opcional — se não informado, mantém o atual.
         */
        UUID venueId
) {
}
