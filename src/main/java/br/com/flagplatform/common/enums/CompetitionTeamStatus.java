package br.com.flagplatform.common.enums;

import lombok.Getter;

@Getter
public enum CompetitionTeamStatus implements PersistableEnum {

    PENDING("PENDING", "Pendente"),
    APPROVED("APPROVED", "Homologado"),
    REJECTED("REJECTED", "Rejeitado");

    private final String code;
    private final String description;

    CompetitionTeamStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
