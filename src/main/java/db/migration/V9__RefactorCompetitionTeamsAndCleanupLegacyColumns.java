package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

/**
 * Migration V9:
 * 1. Refatoração de platform.competition_team:
 *    - Remove coluna legada division_id e sua FK fk_competition_team_division.
 *    - Adiciona colunas status ('PENDING', 'APPROVED', 'REJECTED'), group_name, conference_name, division_name e seed_number.
 *    - Adiciona constraint ck_competition_team_status.
 * 2. Garante constraint ck_competitions_status na tabela platform.competitions suportando 'DISABLED'.
 */
public class V9__RefactorCompetitionTeamsAndCleanupLegacyColumns extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        // ---------------------------------------------------------------------
        // 1. platform.competition_team: remoção de coluna/FK legada division_id
        // ---------------------------------------------------------------------
        dsl.alterTable(DSL.name("platform", "competition_team"))
                .dropConstraintIfExists(DSL.name("fk_competition_team_division"))
                .execute();

        dsl.alterTable(DSL.name("platform", "competition_team"))
                .dropColumnIfExists(DSL.name("division_id"))
                .execute();

        // ---------------------------------------------------------------------
        // 2. platform.competition_team: novas colunas de homologação e alocação
        // ---------------------------------------------------------------------
        dsl.alterTable(DSL.name("platform", "competition_team"))
                .addIfNotExists(DSL.field(
                        DSL.name("status"),
                        SQLDataType.VARCHAR(20).nullable(false).defaultValue(DSL.inline("PENDING"))
                ))
                .execute();

        dsl.alterTable(DSL.name("platform", "competition_team"))
                .dropConstraintIfExists(DSL.name("ck_competition_team_status"))
                .execute();

        dsl.alterTable(DSL.name("platform", "competition_team"))
                .add(DSL.constraint(DSL.name("ck_competition_team_status"))
                        .check(DSL.field(DSL.name("status")).in("PENDING", "APPROVED", "REJECTED")))
                .execute();

        dsl.alterTable(DSL.name("platform", "competition_team"))
                .addIfNotExists(DSL.field(
                        DSL.name("group_name"),
                        SQLDataType.VARCHAR(100).nullable(true)
                ))
                .execute();

        dsl.alterTable(DSL.name("platform", "competition_team"))
                .addIfNotExists(DSL.field(
                        DSL.name("conference_name"),
                        SQLDataType.VARCHAR(100).nullable(true)
                ))
                .execute();

        dsl.alterTable(DSL.name("platform", "competition_team"))
                .addIfNotExists(DSL.field(
                        DSL.name("division_name"),
                        SQLDataType.VARCHAR(100).nullable(true)
                ))
                .execute();

        dsl.alterTable(DSL.name("platform", "competition_team"))
                .addIfNotExists(DSL.field(
                        DSL.name("seed_number"),
                        SQLDataType.INTEGER.nullable(true)
                ))
                .execute();

        // Comentários descritivos
        dsl.execute("COMMENT ON COLUMN platform.competition_team.status IS 'Status da inscrição: PENDING (Pendente), APPROVED (Homologado/Confirmado), REJECTED (Rejeitado)'");
        dsl.execute("COMMENT ON COLUMN platform.competition_team.group_name IS 'Nome do grupo alocado (ex: Grupo A)'");
        dsl.execute("COMMENT ON COLUMN platform.competition_team.conference_name IS 'Nome da conferência alocada (ex: Conferência Leste)'");
        dsl.execute("COMMENT ON COLUMN platform.competition_team.division_name IS 'Nome da divisão alocada dentro da conferência (ex: Divisão Norte)'");
        dsl.execute("COMMENT ON COLUMN platform.competition_team.seed_number IS 'Chaveamento ou classificação prévia da equipe (seed)'");

        // ---------------------------------------------------------------------
        // 3. platform.competitions: consolidação do status DISABLED na constraint
        // ---------------------------------------------------------------------
        dsl.alterTable(DSL.name("platform", "competitions"))
                .dropConstraintIfExists(DSL.name("ck_competitions_status"))
                .execute();

        dsl.alterTable(DSL.name("platform", "competitions"))
                .add(DSL.constraint(DSL.name("ck_competitions_status"))
                        .check(DSL.field(DSL.name("status")).in("DRAFT", "PUBLISHED", "FINISHED", "DISABLED")))
                .execute();
    }
}
