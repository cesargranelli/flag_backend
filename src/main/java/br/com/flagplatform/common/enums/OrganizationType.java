package br.com.flagplatform.common.enums;

import lombok.Getter;

@Getter
public enum OrganizationType implements PersistableEnum {

    FEDERATION("FEDERATION", "Federation"),
    ASSOCIATION("ASSOCIATION", "Association"),
    LEAGUE("LEAGUE", "League"),
    UNIVERSITY("UNIVERSITY", "University"),
    CLUB("CLUB", "Club"),
    OTHER("OTHER", "Other");

    private final String code;
    private final String description;

    OrganizationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
