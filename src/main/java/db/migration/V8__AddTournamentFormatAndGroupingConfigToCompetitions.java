package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

/**
 * Migration V8: Suporte a Formato de Torneio (Pontos Corridos, Playoffs, Grupos + Playoffs)
 * e Agrupamento Flexivel (Sem Agrupamento, Grupos, Conferencias e Divisoes) na tabela platform.competitions.
 */
public class V8__AddTournamentFormatAndGroupingConfigToCompetitions extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        // 1. Adiciona coluna tournament_format com padrao 'ROUND_ROBIN'
        dsl.alterTable(DSL.name("platform", "competitions"))
                .addIfNotExists(DSL.field(
                        DSL.name("tournament_format"),
                        SQLDataType.VARCHAR(30).nullable(false).defaultValue(DSL.inline("ROUND_ROBIN"))
                ))
                .execute();

        // 2. Adiciona constraint de validacao para tournament_format
        dsl.alterTable(DSL.name("platform", "competitions"))
                .dropConstraintIfExists(DSL.name("ck_competitions_tournament_format"))
                .execute();

        dsl.alterTable(DSL.name("platform", "competitions"))
                .add(DSL.constraint(DSL.name("ck_competitions_tournament_format"))
                        .check(DSL.field(DSL.name("tournament_format"))
                                .in("ROUND_ROBIN", "PLAYOFFS", "GROUPS_AND_PLAYOFFS")))
                .execute();

        // 3. Atualiza constraint de validacao para grouping_type (adicionando NONE e CONFERENCES)
        dsl.alterTable(DSL.name("platform", "competitions"))
                .dropConstraintIfExists(DSL.name("ck_competitions_grouping_type"))
                .execute();

        dsl.alterTable(DSL.name("platform", "competitions"))
                .add(DSL.constraint(DSL.name("ck_competitions_grouping_type"))
                        .check(DSL.field(DSL.name("grouping_type")).isNull()
                                .or(DSL.field(DSL.name("grouping_type")).in("NONE", "GROUPS", "CONFERENCES", "DIVISIONS"))))
                .execute();

        // 4. Adiciona coluna grouping_config JSONB para estrutura declarativa
        dsl.alterTable(DSL.name("platform", "competitions"))
                .addIfNotExists(DSL.field(
                        DSL.name("grouping_config"),
                        SQLDataType.JSONB.nullable(true)
                ))
                .execute();

        // 5. Comentarios descritivos nas novas colunas
        dsl.execute("COMMENT ON COLUMN platform.competitions.tournament_format IS 'Formato do campeonato: ROUND_ROBIN (Pontos Corridos), PLAYOFFS (Playoffs), GROUPS_AND_PLAYOFFS (Grupos + Playoffs)'");
        dsl.execute("COMMENT ON COLUMN platform.competitions.grouping_type IS 'Modelo de agrupamento: NONE (Tabela Unica), GROUPS (Grupos), CONFERENCES (Conferencias / Divisoes)'");
        dsl.execute("COMMENT ON COLUMN platform.competitions.grouping_config IS 'Estrutura JSON com os grupos declarados ou conferencias com suas respectivas divisoes'");
    }
}
