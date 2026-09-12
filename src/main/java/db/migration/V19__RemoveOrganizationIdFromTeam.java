package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * Migration V19: Remove a coluna organization_id da tabela platform.team.
 *
 * <p>Equipes esportivas agora referenciam apenas clubes/instituições
 * via club_id. O campo organization_id era redundante e causava
 * confusão entre nome da agremiação e nome da organização.
 */
public class V19__RemoveOrganizationIdFromTeam extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        // Remover FK organization_id da tabela platform.team
        dsl.alterTable(DSL.name("platform", "team"))
                .dropConstraintIfExists(DSL.name("fk_team_organization"))
                .execute();

        // Remover coluna organization_id
        dsl.alterTable(DSL.name("platform", "team"))
                .dropColumnIfExists(DSL.name("organization_id"))
                .execute();
    }
}
