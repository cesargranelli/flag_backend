package br.com.flagplatform.person.entity;

import br.com.flagplatform.common.enums.AthleteStatus;
import br.com.flagplatform.common.enums.Gender;
import br.com.flagplatform.common.enums.PersonRole;
import br.com.flagplatform.common.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "persons")
public class PersonEntity extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 14)
    private String cpf;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(nullable = false, length = 20)
    private AthleteStatus status;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 20)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PersonRole role;
}
