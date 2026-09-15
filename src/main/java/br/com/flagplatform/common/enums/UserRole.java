package br.com.flagplatform.common.enums;

import lombok.Getter;

@Getter
public enum UserRole implements PersistableEnum {

    ADMIN("ADMIN", "Administrador da plataforma"),
    ORGANIZER("ORGANIZER", "Administrador de federação, associação ou liga"),
    COMMISSIONER("COMMISSIONER", "Comissário/delegado das partidas"),
    REFEREE("REFEREE", "Árbitro das partidas"),
    MANAGER("MANAGER", "Gestor do clube ou universidade"),
    FAN("FAN", "Torcedor");

    private final String code;
    private final String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
