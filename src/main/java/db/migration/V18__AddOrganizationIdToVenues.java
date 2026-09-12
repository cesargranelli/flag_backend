package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.Connection;

/**
 * Migration V18: Add organization_id to venues table.
 *
 * <p>Adiciona o campo organization_id à tabela platform.venues,
 * permitindo associar campos de jogo a organizações. O campo é nullable
 * para compatibilidade com dados existentes.
 */
public class V18__AddOrganizationIdToVenues extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        // Adicionar coluna organization_id (UUID, nullable)
        dsl.alterTable(DSL.name("platform", "venues"))
                .add(
                        DSL.field(DSL.name("organization_id"), SQLDataType.UUID)
                )
                .execute();

        // Criar índice para busca por organização
        dsl.createIndexIfNotExists(DSL.name("idx_venues_organization_id"))
                .on(DSL.table(DSL.name("platform", "venues")),
                        DSL.field(DSL.name("organization_id")))
                .execute();
    }
}