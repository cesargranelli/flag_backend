package br.com.flagplatform.common.enums;

import lombok.Getter;

@Getter
public enum UserRole implements PersistableEnum {

    ADMIN("ADMIN", "Administrador"),
    ORGANIZER("ORGANIZER", "Organizador"),
    MESA("MESA", "Mesa"),
    ADMIN_LIGA("ADMIN_LIGA", "Administrador da Liga"),
    REFEREE("REFEREE", "Árbitro"),
    CLUB_MANAGER("CLUB_MANAGER", "Gestor do Clube"),
    FAN("FAN", "Torcedor");

    private final String code;
    private final String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
