package br.com.flagplatform.common.enums;

import lombok.Getter;

@Getter
public enum UserRole implements PersistableEnum {

    ADMIN("ADMIN", "Administrador"),
    ORGANIZER("ORGANIZER", "Organizador"),
    COMMISSIONER("COMMISSIONER", "Delegado"),
    ADMIN_INSTITUTION("ADMIN_INSTITUTION", "Administrador da Liga"),
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
