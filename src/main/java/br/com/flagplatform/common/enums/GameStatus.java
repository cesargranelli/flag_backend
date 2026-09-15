package br.com.flagplatform.common.enums;

import lombok.Getter;

@Getter
public enum GameStatus implements PersistableEnum {

    SCHEDULED("SCHEDULED", "Agendado"),
    OPEN("OPEN", "Aberto"),
    IN_PROGRESS("IN_PROGRESS", "Em andamento"),
    CONFERENCE("CONFERENCE", "Em conferência"),
    FINISHED("FINISHED", "Finalizado"),
    CANCELLED("CANCELLED", "Cancelado"),
    POSTPONED("POSTPONED", "Adiado");

    private final String code;
    private final String description;

    GameStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
