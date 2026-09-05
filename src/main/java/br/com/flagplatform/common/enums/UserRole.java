package br.com.flagplatform.common.enums;

import lombok.Getter;

@Getter
public enum UserRole implements PersistableEnum {

    SUPER_ADMIN("SUPER_ADMIN", "Super Administrador"),
    ORG_ADMIN("ORGANIZER", "Organizador"),
    MANAGER("MANAGER", "Gerente"),
    USER("USER", "Usuário");

    private final String code;
    private final String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
