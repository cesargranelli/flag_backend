package br.com.flagplatform.roster.entity;

import br.com.flagplatform.common.enums.RosterStatus;
import br.com.flagplatform.common.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "roster")
public class RosterEntity extends BaseEntity {

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    /**
     * Id da competição associada ao elenco.
     * Nullable: quando nulo, representa o elenco-base permanente do time
     * (sem vínculo a uma competição específica).
     */
    @Column(name = "competition_id")
    private UUID competitionId;

    @Column(length = 255)
    private String name;

    @Column(nullable = false, length = 50)
    private String season;

    @Column(nullable = false, length = 20)
    private RosterStatus status;
}
