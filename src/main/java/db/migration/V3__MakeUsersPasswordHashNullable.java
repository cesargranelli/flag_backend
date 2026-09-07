package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * Migration V3: Torna password_hash opcional em platform.users (ADR-008).
 * Usa DSL do jOOQ (ADR-007) para evitar lock-in de SQL nativo.
 */
public class V3__MakeUsersPasswordHashNullable extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        DSLContext dsl = DSL.using(context.getConnection(), SQLDialect.POSTGRES);
        dsl.alterTable(DSL.name("platform", "users"))
                .alter(DSL.field(DSL.name("password_hash")))
                .dropNotNull()
                .execute();
    }
}
