package br.com.flagplatform.common.enums;

import lombok.Getter;

@Getter
public enum InstitutionType implements PersistableEnum {

    CLUB("CLUB", "Club"),
    UNIVERSITY("UNIVERSITY", "University");

    private final String code;
    private final String description;

    InstitutionType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
