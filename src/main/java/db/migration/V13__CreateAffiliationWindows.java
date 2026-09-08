package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

/**
 * Migration V13: Cria a tabela platform.affiliation_windows para controlar
 * a abertura e encerramento dos periodos de filiacao por organizacao e temporada.
 */
public class V13__CreateAffiliationWindows extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        dsl.createTableIfNotExists(DSL.name("platform", "affiliation_windows"))
                .column(DSL.field(DSL.name("id"), SQLDataType.UUID.nullable(false).defaultValue(DSL.function("gen_random_uuid", SQLDataType.UUID))))
                .column(DSL.field(DSL.name("organization_id"), SQLDataType.UUID.nullable(false)))
                .column(DSL.field(DSL.name("season"), SQLDataType.VARCHAR(20).nullable(false).defaultValue(DSL.inline("2026"))))
                .column(DSL.field(DSL.name("title"), SQLDataType.VARCHAR(150).nullable(false)))
                .column(DSL.field(DSL.name("start_date"), SQLDataType.LOCALDATE.nullable(false)))
                .column(DSL.field(DSL.name("end_date"), SQLDataType.LOCALDATE.nullable(false)))
                .column(DSL.field(DSL.name("status"), SQLDataType.VARCHAR(20).nullable(false).defaultValue(DSL.inline("OPEN"))))
                .column(DSL.field(DSL.name("instructions"), SQLDataType.VARCHAR(1000)))
                .column(DSL.field(DSL.name("created_at"), SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.currentTimestamp())))
                .column(DSL.field(DSL.name("created_by"), SQLDataType.VARCHAR(150)))
                .column(DSL.field(DSL.name("updated_at"), SQLDataType.TIMESTAMP))
                .constraint(DSL.constraint(DSL.name("pk_affiliation_windows")).primaryKey(DSL.name("id")))
                .constraint(DSL.constraint(DSL.name("fk_affil_win_organization"))
                        .foreignKey(DSL.name("organization_id"))
                        .references(DSL.name("platform", "organizations"), DSL.name("id"))
                        .onDeleteCascade())
                .constraint(DSL.constraint(DSL.name("uk_affil_win_org_season"))
                        .unique(DSL.name("organization_id"), DSL.name("season")))
                .execute();

        dsl.createIndexIfNotExists(DSL.name("idx_affil_win_org_season_status"))
                .on(DSL.table(DSL.name("platform", "affiliation_windows")),
                        DSL.field(DSL.name("organization_id")),
                        DSL.field(DSL.name("season")),
                        DSL.field(DSL.name("status")))
                .execute();

        // Cria janela padrão 2026 ABERTA para todas as organizações já existentes para evitar travamentos legados
        dsl.execute("""
                INSERT INTO platform.affiliation_windows (organization_id, season, title, start_date, end_date, status, instructions)
                SELECT id, '2026', 'Filiação Oficial Temporada 2026', CURRENT_DATE, CURRENT_DATE + INTERVAL '180 days', 'OPEN', 'Período regular de filiação anual de clubes e universidades.'
                FROM platform.organizations
                ON CONFLICT (organization_id, season) DO NOTHING
                """);
    }
}
