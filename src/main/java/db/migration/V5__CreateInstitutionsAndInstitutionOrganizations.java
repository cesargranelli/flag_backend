package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

/**
 * Migration V5: Cria platform.institutions e institution_organizations (ADR-009).
 * Usa DSL do jOOQ (ADR-007).
 */
public class V5__CreateInstitutionsAndInstitutionOrganizations extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        DSLContext dsl = DSL.using(context.getConnection(), SQLDialect.POSTGRES);

        dsl.createTableIfNotExists(DSL.name("platform", "institutions"))
                .column(DSL.field(DSL.name("id"), SQLDataType.UUID.nullable(false)))
                .column(DSL.field(DSL.name("name"), SQLDataType.VARCHAR(255).nullable(false)))
                .column(DSL.field(DSL.name("type"), SQLDataType.VARCHAR(20)))
                .column(DSL.field(DSL.name("colors"), SQLDataType.VARCHAR.getArrayDataType()))
                .column(DSL.field(DSL.name("status"), SQLDataType.VARCHAR(20).nullable(false).defaultValue(DSL.inline("ACTIVE"))))
                .column(DSL.field(DSL.name("created_at"), SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.currentTimestamp())))
                .column(DSL.field(DSL.name("updated_at"), SQLDataType.TIMESTAMP))
                .constraint(DSL.constraint(DSL.name("pk_institutions")).primaryKey(DSL.name("id")))
                .constraint(DSL.constraint(DSL.name("ck_institutions_type")).check(DSL.field(DSL.name("type")).in("CLUB", "UNIVERSITY")))
                .execute();

        dsl.createTableIfNotExists(DSL.name("platform", "institution_organizations"))
                .column(DSL.field(DSL.name("institution_id"), SQLDataType.UUID.nullable(false)))
                .column(DSL.field(DSL.name("organization_id"), SQLDataType.UUID.nullable(false)))
                .column(DSL.field(DSL.name("created_at"), SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.currentTimestamp())))
                .column(DSL.field(DSL.name("created_by"), SQLDataType.UUID))
                .constraint(DSL.constraint(DSL.name("pk_institution_organizations")).primaryKey(DSL.name("institution_id"), DSL.name("organization_id")))
                .constraint(DSL.constraint(DSL.name("fk_inst_org_institution")).foreignKey(DSL.name("institution_id")).references(DSL.name("platform", "institutions"), DSL.name("id")).onDeleteCascade())
                .constraint(DSL.constraint(DSL.name("fk_inst_org_organization")).foreignKey(DSL.name("organization_id")).references(DSL.name("platform", "organizations"), DSL.name("id")).onDeleteCascade())
                .execute();

        dsl.createIndexIfNotExists(DSL.name("idx_institution_organizations_organization_id"))
                .on(DSL.table(DSL.name("platform", "institution_organizations")), DSL.field(DSL.name("organization_id")))
                .execute();
    }
}
