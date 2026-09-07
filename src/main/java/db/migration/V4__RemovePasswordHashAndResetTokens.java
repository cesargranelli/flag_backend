package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * Migration V4: Remove password_hash e password_reset_tokens (ADR-008).
 * Usa DSL do jOOQ (ADR-007) para evitar lock-in.
 */
public class V4__RemovePasswordHashAndResetTokens extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        DSLContext dsl = DSL.using(context.getConnection(), SQLDialect.POSTGRES);
        dsl.dropTableIfExists(DSL.name("platform", "password_reset_tokens")).execute();
        dsl.alterTable(DSL.name("platform", "users"))
                .dropColumnIfExists(DSL.field(DSL.name("password_hash")))
                .execute();
    }
}
