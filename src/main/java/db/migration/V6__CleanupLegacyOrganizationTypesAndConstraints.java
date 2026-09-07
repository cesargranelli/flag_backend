package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * Migration V6: Saneia registros legados de organization_type na tabela platform.organizations
 * e atualiza a constraint ck_organizations_type conforme ADR-007 (jOOQ DSL) e ADR-009.
 */
public class V6__CleanupLegacyOrganizationTypesAndConstraints extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        // 1. Saneia tipos legados para ASSOCIATION e inativa o registro para nao poluir listagens ativas
        dsl.update(DSL.table(DSL.name("platform", "organizations")))
                .set(DSL.field(DSL.name("organization_type")), "ASSOCIATION")
                .set(DSL.field(DSL.name("status")), "INACTIVE")
                .where(DSL.field(DSL.name("organization_type"))
                        .notIn("FEDERATION", "LEAGUE", "ASSOCIATION")
                        .or(DSL.field(DSL.name("organization_type")).isNull()))
                .execute();

        // 2. Remove a constraint legada da V1 se existir
        dsl.alterTable(DSL.name("platform", "organizations"))
                .dropConstraintIfExists(DSL.name("ck_organizations_type"))
                .execute();

        // 3. Cria a nova constraint restrita aos tipos validos atuais (ADR-009)
        dsl.alterTable(DSL.name("platform", "organizations"))
                .add(DSL.constraint(DSL.name("ck_organizations_type"))
                        .check(DSL.field(DSL.name("organization_type"))
                                .in("FEDERATION", "LEAGUE", "ASSOCIATION")))
                .execute();
    }
}
