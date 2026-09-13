package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * Migration V15: Remove colunas nickname e number da tabela athletes.
 *
 * <p>Esses dados sao de responsabilidade do elenco (team_roster),
 * nao do atleta como entidade generica.
 */
public class V15__RemoveAthleteNickNumber extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        dsl.alterTable(DSL.name("platform", "athletes"))
                .dropColumnIfExists(DSL.field(DSL.name("nickname")))
                .execute();

        dsl.alterTable(DSL.name("platform", "athletes"))
                .dropColumnIfExists(DSL.field(DSL.name("number")))
                .execute();
    }
}
