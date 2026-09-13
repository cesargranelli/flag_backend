package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * Migration V10: Torna organization_id nullable em platform.team e adiciona FK para platform.institutions.
 * Equipes esportivas pertencem diretamente a agremiações (institutions / clubes / universidades).
 */
public class V10__AllowNullableOrganizationIdInTeam extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        // 1. Tornar organization_id NULLABLE
        dsl.alterTable(DSL.name("platform", "team"))
                .alter(DSL.field(DSL.name("organization_id")))
                .dropNotNull()
                .execute();

        // 2. Remover FK legada para platform.clubs se existir
        dsl.alterTable(DSL.name("platform", "team"))
                .dropConstraintIfExists(DSL.name("fk_team_club"))
                .execute();

        // 3. Adicionar FK fk_team_institution apontando club_id para platform.institutions
        dsl.alterTable(DSL.name("platform", "team"))
                .add(DSL.constraint(DSL.name("fk_team_institution"))
                        .foreignKey(DSL.name("club_id"))
                        .references(DSL.name("platform", "institutions"), DSL.name("id"))
                        .onDeleteCascade())
                .execute();
    }
}