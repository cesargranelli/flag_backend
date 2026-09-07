package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

/**
 * Migration V7: Alinha a tabela platform.institutions com a estrutura de platform.organizations
 * (campos cadastrais completos e 4 cores nomeadas).
 */
public class V7__AlignInstitutionsWithOrganizations extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        // 1. Adicionar colunas cadastrais e de identidade visual
        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("legal_name"), SQLDataType.VARCHAR(255)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("trade_name"), SQLDataType.VARCHAR(255)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("abbreviation"), SQLDataType.VARCHAR(20)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("document"), SQLDataType.VARCHAR(20)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("document_type"), SQLDataType.VARCHAR(10)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("president_name"), SQLDataType.VARCHAR(150)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("president_cpf"), SQLDataType.VARCHAR(14)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("email"), SQLDataType.VARCHAR(150)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("phone"), SQLDataType.VARCHAR(30)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("website"), SQLDataType.VARCHAR(255)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("instagram"), SQLDataType.VARCHAR(100)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("country"), SQLDataType.VARCHAR(2).defaultValue(DSL.inline("BR"))))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("state"), SQLDataType.VARCHAR(100)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("city"), SQLDataType.VARCHAR(100)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("logo_url"), SQLDataType.VARCHAR(500)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("primary_color"), SQLDataType.VARCHAR(7)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("secondary_color"), SQLDataType.VARCHAR(7)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("tertiary_color"), SQLDataType.VARCHAR(7)))
                .execute();

        dsl.alterTable(DSL.name("platform", "institutions"))
                .addColumn(DSL.field(DSL.name("quaternary_color"), SQLDataType.VARCHAR(7)))
                .execute();

        // 2. Popular trade_name e legal_name com o valor de name caso existam registros legados
        dsl.execute("UPDATE platform.institutions SET trade_name = name WHERE trade_name IS NULL");
        dsl.execute("UPDATE platform.institutions SET legal_name = name WHERE legal_name IS NULL");

        // 3. Tornar trade_name NOT NULL para paridade com platform.organizations
        dsl.alterTable(DSL.name("platform", "institutions"))
                .alter(DSL.field(DSL.name("trade_name")))
                .setNotNull()
                .execute();

        // 4. Índices para performance em buscas
        dsl.createIndexIfNotExists(DSL.name("idx_institutions_trade_name"))
                .on(DSL.table(DSL.name("platform", "institutions")), DSL.field(DSL.name("trade_name")))
                .execute();

        dsl.createIndexIfNotExists(DSL.name("idx_institutions_document"))
                .on(DSL.table(DSL.name("platform", "institutions")), DSL.field(DSL.name("document")))
                .execute();
    }
}
