package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

/**
 * Migration V12: Cria a tabela platform.institution_affiliations para suportar
 * o ciclo de solicitacao e aprovacao de filiacao de agremiacoes a organizacoes por temporada.
 */
public class V12__CreateInstitutionAffiliations extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        dsl.createTableIfNotExists(DSL.name("platform", "institution_affiliations"))
                .column(DSL.field(DSL.name("id"), SQLDataType.UUID.nullable(false).defaultValue(DSL.function("gen_random_uuid", SQLDataType.UUID))))
                .column(DSL.field(DSL.name("institution_id"), SQLDataType.UUID.nullable(false)))
                .column(DSL.field(DSL.name("organization_id"), SQLDataType.UUID.nullable(false)))
                .column(DSL.field(DSL.name("season"), SQLDataType.VARCHAR(20).nullable(false).defaultValue(DSL.inline("2026"))))
                .column(DSL.field(DSL.name("status"), SQLDataType.VARCHAR(20).nullable(false).defaultValue(DSL.inline("PENDING"))))
                .column(DSL.field(DSL.name("rejection_reason"), SQLDataType.VARCHAR(500)))
                .column(DSL.field(DSL.name("requested_at"), SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.currentTimestamp())))
                .column(DSL.field(DSL.name("requested_by"), SQLDataType.VARCHAR(150)))
                .column(DSL.field(DSL.name("reviewed_at"), SQLDataType.TIMESTAMP))
                .column(DSL.field(DSL.name("reviewed_by"), SQLDataType.VARCHAR(150)))
                .column(DSL.field(DSL.name("created_at"), SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.currentTimestamp())))
                .column(DSL.field(DSL.name("updated_at"), SQLDataType.TIMESTAMP))
                .constraint(DSL.constraint(DSL.name("pk_institution_affiliations")).primaryKey(DSL.name("id")))
                .constraint(DSL.constraint(DSL.name("fk_inst_affil_institution"))
                        .foreignKey(DSL.name("institution_id"))
                        .references(DSL.name("platform", "institutions"), DSL.name("id"))
                        .onDeleteCascade())
                .constraint(DSL.constraint(DSL.name("fk_inst_affil_organization"))
                        .foreignKey(DSL.name("organization_id"))
                        .references(DSL.name("platform", "organizations"), DSL.name("id"))
                        .onDeleteCascade())
                .constraint(DSL.constraint(DSL.name("uk_affiliation_inst_org_season"))
                        .unique(DSL.name("institution_id"), DSL.name("organization_id"), DSL.name("season")))
                .execute();

        dsl.createIndexIfNotExists(DSL.name("idx_inst_affil_org_season"))
                .on(DSL.table(DSL.name("platform", "institution_affiliations")),
                        DSL.field(DSL.name("organization_id")),
                        DSL.field(DSL.name("season")),
                        DSL.field(DSL.name("status")))
                .execute();

        dsl.createIndexIfNotExists(DSL.name("idx_inst_affil_inst_season"))
                .on(DSL.table(DSL.name("platform", "institution_affiliations")),
                        DSL.field(DSL.name("institution_id")),
                        DSL.field(DSL.name("season")))
                .execute();

        // Migrar dados existentes de platform.institution_organizations como filiações APROVADAS na temporada 2026
        dsl.execute("""
                INSERT INTO platform.institution_affiliations (institution_id, organization_id, season, status, requested_at, reviewed_at)
                SELECT io.institution_id, io.organization_id, '2026', 'APPROVED', io.created_at, io.created_at
                FROM platform.institution_organizations io
                ON CONFLICT (institution_id, organization_id, season) DO NOTHING
                """);
    }
}
