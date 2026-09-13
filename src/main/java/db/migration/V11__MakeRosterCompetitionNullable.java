package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * V11: Suporte a elenco-base de time sem competicao associada.
 * <p>
 * Alteracoes em platform.roster:
 * <ul>
 *   <li>Remove a constraint UNIQUE (team_id, competition_id) e a FK fk_roster_competition.</li>
 *   <li>Torna competition_id nullable.</li>
 *   <li>Recria a FK com nullable (permite NULL).</li>
 *   <li>Cria indice unico PARCIAL para elenco-base: (team_id) WHERE competition_id IS NULL.</li>
 *   <li>Cria indice unico PARCIAL para elenco de competicao: (team_id, competition_id) WHERE competition_id IS NOT NULL.</li>
 * </ul>
 */
public class V11__MakeRosterCompetitionNullable extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        // 1. Remover constraint UNIQUE legada (team_id, competition_id)
        dsl.alterTable(DSL.name("platform", "roster"))
                .dropConstraintIfExists(DSL.name("uk_roster_team_competition"))
                .execute();

        // 2. Remover FK fk_roster_competition para poder tornar a coluna nullable
        dsl.alterTable(DSL.name("platform", "roster"))
                .dropConstraintIfExists(DSL.name("fk_roster_competition"))
                .execute();

        // 3. Tornar competition_id nullable
        dsl.alterTable(DSL.name("platform", "roster"))
                .alter(DSL.field(DSL.name("competition_id")))
                .dropNotNull()
                .execute();

        // 4. Recriar FK com nullable (competition_id -> platform.competitions.id)
        dsl.alterTable(DSL.name("platform", "roster"))
                .add(DSL.constraint(DSL.name("fk_roster_competition"))
                        .foreignKey(DSL.name("competition_id"))
                        .references(DSL.name("platform", "competitions"), DSL.name("id")))
                .execute();

        // 5. Indice unico parcial -- elenco base: um por time quando competition_id IS NULL
        dsl.execute(
                "CREATE UNIQUE INDEX IF NOT EXISTS uk_roster_team_no_competition " +
                "ON platform.roster (team_id) " +
                "WHERE competition_id IS NULL"
        );

        // 6. Indice unico parcial -- elenco de competicao: um por (time, competicao)
        dsl.execute(
                "CREATE UNIQUE INDEX IF NOT EXISTS uk_roster_team_competition " +
                "ON platform.roster (team_id, competition_id) " +
                "WHERE competition_id IS NOT NULL"
        );
    }
}
