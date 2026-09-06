package br.com.flagplatform.institution.entity;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstitutionType type;

    @Column(columnDefinition = "text[]")
    private String[] colors;

    @Column(nullable = false)
    private String status;
}
