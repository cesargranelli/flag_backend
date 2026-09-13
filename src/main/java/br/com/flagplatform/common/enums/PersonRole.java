package br.com.flagplatform.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PersonRole {
    ATHLETE("Atleta"),
    COACH("Técnico"),
    TECHNICAL_STAFF("Comissão Técnica"),
    REFEREE("Árbitro"),
    DELEGATE("Delegado"),
    COMMISSIONER("Comissário");

    private final String label;

    PersonRole(String label) { this.label = label; }

    @JsonValue
    public String toJson() { return name(); }

    public String getLabel() { return label; }
}
