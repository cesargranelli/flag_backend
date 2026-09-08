package br.com.flagplatform.team.entity;

import br.com.flagplatform.common.enums.CompetitionTeamStatus;
import br.com.flagplatform.common.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "competition_team",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_competition_team",
                        columnNames = {"competition_id", "team_id"}
                )
        }
)
public class CompetitionTeamEntity extends BaseEntity {

    @Column(name = "competition_id", nullable = false)
    private UUID competitionId;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "status", nullable = false)
    private CompetitionTeamStatus status = CompetitionTeamStatus.PENDING;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "conference_name")
    private String conferenceName;

    @Column(name = "division_name")
    private String divisionName;

    @Column(name = "seed_number")
    private Integer seedNumber;
}
