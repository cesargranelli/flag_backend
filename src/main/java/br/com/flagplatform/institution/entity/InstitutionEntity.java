package br.com.flagplatform.institution.entity;

import br.com.flagplatform.common.enums.DocumentType;
import br.com.flagplatform.common.enums.InstitutionType;
import br.com.flagplatform.common.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "institutions", schema = "platform")
public class InstitutionEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "trade_name", nullable = false)
    private String tradeName;

    @Column(name = "legal_name")
    private String legalName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstitutionType type;

    @Column(length = 20)
    private String abbreviation;

    @Column(length = 20)
    private String document;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", length = 10)
    private DocumentType documentType;

    @Column(name = "president_name", length = 150)
    private String presidentName;

    @Column(name = "president_cpf", length = 14)
    private String presidentCpf;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String website;

    @Column(length = 100)
    private String instagram;

    @Column(length = 2)
    private String country = "BR";

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String city;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "primary_color", length = 7)
    private String primaryColor;

    @Column(name = "secondary_color", length = 7)
    private String secondaryColor;

    @Column(name = "tertiary_color", length = 7)
    private String tertiaryColor;

    @Column(name = "quaternary_color", length = 7)
    private String quaternaryColor;

    @Column(columnDefinition = "text[]")
    private String[] colors;

    @Column(nullable = false)
    private String status = "ACTIVE";
}
