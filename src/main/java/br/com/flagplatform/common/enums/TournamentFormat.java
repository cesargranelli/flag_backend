package br.com.flagplatform.common.enums;

import lombok.Getter;

@Getter
public enum TournamentFormat implements PersistableEnum {

    ROUND_ROBIN("ROUND_ROBIN", "Pontos Corridos"),
    PLAYOFFS("PLAYOFFS", "Playoffs"),
    GROUPS_AND_PLAYOFFS("GROUPS_AND_PLAYOFFS", "Grupos + Playoffs");

    private final String code;
    private final String description;

    TournamentFormat(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
