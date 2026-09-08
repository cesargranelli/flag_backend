package br.com.flagplatform.common.enums;

import lombok.Getter;

@Getter
public enum GroupingType implements PersistableEnum {

    NONE("NONE", "Sem Agrupamento"),
    GROUPS("GROUPS", "Grupos"),
    CONFERENCES("CONFERENCES", "Conferências & Divisões"),
    DIVISIONS("DIVISIONS", "Divisões");

    private final String code;
    private final String description;

    GroupingType(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
