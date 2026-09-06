package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

/**
 * Migration V2: Cria a tabela platform.clubs e adiciona a referência club_id na tabela platform.team.
 * Conforme ADR-003 (Hierarquia de 5 níveis) e ADR-007 (Migrações Flyway em Java com JOOQ).
 */
public class V2__CreateClubsAndRefactorTeams extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        // a) Criação da tabela platform.clubs
        dsl.createTable(DSL.name("platform", "clubs"))
                .column(DSL.field(DSL.name("id"), SQLDataType.UUID.nullable(false).defaultValue(DSL.uuid())))
                .column(DSL.field(DSL.name("organization_id"), SQLDataType.UUID.nullable(false)))
                .column(DSL.field(DSL.name("name"), SQLDataType.VARCHAR(255).nullable(false)))
                .column(DSL.field(DSL.name("short_name"), SQLDataType.VARCHAR(50)))
                .column(DSL.field(DSL.name("sport_name"), SQLDataType.VARCHAR(255)))
                .column(DSL.field(DSL.name("logo_url"), SQLDataType.VARCHAR(500)))
                .column(DSL.field(DSL.name("document"), SQLDataType.VARCHAR(20)))
                .column(DSL.field(DSL.name("document_type"), SQLDataType.VARCHAR(10)))
                .column(DSL.field(DSL.name("president_name"), SQLDataType.VARCHAR(150)))
                .column(DSL.field(DSL.name("president_cpf"), SQLDataType.VARCHAR(14)))
                .column(DSL.field(DSL.name("status"), SQLDataType.VARCHAR(20).defaultValue(DSL.inline("ACTIVE"))))
                .column(DSL.field(DSL.name("created_at"), SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.currentTimestamp())))
                .column(DSL.field(DSL.name("updated_at"), SQLDataType.TIMESTAMP))
                .column(DSL.field(DSL.name("created_by"), SQLDataType.UUID))
                .column(DSL.field(DSL.name("updated_by"), SQLDataType.UUID))
                .primaryKey(DSL.name("id"))
                .constraint(DSL.constraint(DSL.name("fk_clubs_organization"))
                        .foreignKey(DSL.name("organization_id"))
                        .references(DSL.name("platform", "organizations"), DSL.name("id")))
                .execute();

        // b) Alteração da tabela platform.team: adicionar coluna club_id (UUID, NULL)
        dsl.alterTable(DSL.name("platform", "team"))
                .addColumn(DSL.field(DSL.name("club_id"), SQLDataType.UUID))
                .execute();

        // c) Alteração da tabela platform.team: adicionar Foreign Key fk_team_club referenciando platform.clubs(id)
        dsl.alterTable(DSL.name("platform", "team"))
                .add(DSL.constraint(DSL.name("fk_team_club"))
                        .foreignKey(DSL.name("club_id"))
                        .references(DSL.name("platform", "clubs"), DSL.name("id")))
                .execute();
    }
}
