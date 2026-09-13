package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

/**
 * Migration V18: Renomeia participants para persons e reestrutura modelo.
 *
 * <p>Alteracoes:
 * <ul>
 *   <li>Renomeia tabela participants → persons</li>
 *   <li>Renomeia tabela participant_positions → person_positions</li>
 *   <li>Renomeia coluna participant_id → person_id em person_positions</li>
 *   <li>Adiciona coluna role (VARCHAR(30)) em persons com default ATHLETE</li>
 *   <li>Adiciona coluna positions (VARCHAR(255)) em team_roster para ate 3 posicoes</li>
 *   <li>Migra dados de person_positions para team_roster.positions</li>
 *   <li>Remove tabela person_positions (posicoes agora vivem no elenco)</li>
 *   <li>Cria tabela game_participants para arbitros, delegados, etc.</li>
 *   <li>Atualiza todas as constraints e indices</li>
 * </ul>
 */
public class V17__RenameParticipantsToPersons extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        // =====================================================================
        // 1. Dropar FKs que dependem da PK de participants (antes de renomear)
        // =====================================================================
        dsl.execute("ALTER TABLE platform.participant_positions DROP CONSTRAINT IF EXISTS participant_positions_participant_id_fkey");
        dsl.execute("ALTER TABLE platform.team_roster DROP CONSTRAINT IF EXISTS fk_team_roster_participant");
        dsl.execute("ALTER TABLE platform.checkins DROP CONSTRAINT IF EXISTS fk_checkins_participant");

        // =====================================================================
        // 2. Renomear tabelas
        // =====================================================================
        dsl.alterTable(DSL.name("platform", "participants"))
                .renameTo(DSL.name("platform", "persons"))
                .execute();

        dsl.alterTable(DSL.name("platform", "participant_positions"))
                .renameTo(DSL.name("platform", "person_positions"))
                .execute();

        // =====================================================================
        // 3. Renomear constraints e indices
        // =====================================================================
        dsl.execute("ALTER TABLE platform.persons DROP CONSTRAINT IF EXISTS participants_pkey");
        dsl.execute("ALTER TABLE platform.persons ADD CONSTRAINT persons_pkey PRIMARY KEY (id)");

        dsl.execute("DROP INDEX IF EXISTS platform.uk_participants_cpf");
        dsl.execute("CREATE UNIQUE INDEX uk_persons_cpf ON platform.persons (cpf) WHERE cpf IS NOT NULL");

        // Renomear coluna participant_id → person_positions
        dsl.execute("ALTER TABLE platform.person_positions RENAME COLUMN participant_id TO person_id");

        // Recriar FK de person_positions
        dsl.execute("ALTER TABLE platform.person_positions ADD CONSTRAINT person_positions_person_id_fkey FOREIGN KEY (person_id) REFERENCES platform.persons(id)");

        // =====================================================================
        // 4. Adicionar coluna role em persons
        // =====================================================================
        dsl.alterTable(DSL.name("platform", "persons"))
                .add(DSL.field(DSL.name("role"), SQLDataType.VARCHAR(30).nullable(false).defaultValue(DSL.inline("ATHLETE"))))
                .execute();

        // Atualizar registros existentes para ATHLETE (comportamento legado)
        dsl.execute("UPDATE platform.persons SET role = 'ATHLETE' WHERE role IS NULL");

        // =====================================================================
        // 5. Adicionar coluna positions em team_roster
        // =====================================================================
        dsl.alterTable(DSL.name("platform", "team_roster"))
                .add(DSL.field(DSL.name("positions"), SQLDataType.VARCHAR(255)))
                .execute();

        // Migrar dados de person_positions para team_roster.positions
        // Formato: concatenar posicoes separadas por virgula
        dsl.execute("""
                UPDATE platform.team_roster tr
                SET positions = sub.positions_csv
                FROM (
                    SELECT ro.id AS roster_entry_id,
                           string_agg(pp.position, ',' ORDER BY pp.position) AS positions_csv
                    FROM platform.team_roster ro
                    JOIN platform.person_positions pp ON pp.person_id = ro.athlete_id
                    GROUP BY ro.id
                ) sub
                WHERE tr.id = sub.roster_entry_id
                """);

        // =====================================================================
        // 6. Dropar tabela person_positions (posicoes agora vivem no elenco)
        // =====================================================================
        dsl.execute("DROP TABLE IF EXISTS platform.person_positions CASCADE");

        // =====================================================================
        // 7. Recriar FKs com nomes atualizados
        // =====================================================================
        dsl.execute("ALTER TABLE platform.team_roster ADD CONSTRAINT fk_team_roster_person FOREIGN KEY (athlete_id) REFERENCES platform.persons(id)");
        dsl.execute("ALTER TABLE platform.checkins ADD CONSTRAINT fk_checkins_person FOREIGN KEY (athlete_id) REFERENCES platform.persons(id)");

        // =====================================================================
        // 8. Criar tabela game_participants
        // =====================================================================
        dsl.createTableIfNotExists(DSL.name("platform", "game_participants"))
                .column(DSL.field(DSL.name("id"), SQLDataType.UUID.nullable(false)
                        .defaultValue(DSL.function("gen_random_uuid", SQLDataType.UUID))))
                .column(DSL.field(DSL.name("game_id"), SQLDataType.UUID.nullable(false)))
                .column(DSL.field(DSL.name("person_id"), SQLDataType.UUID.nullable(false)))
                .column(DSL.field(DSL.name("role"), SQLDataType.VARCHAR(30).nullable(false)))
                .column(DSL.field(DSL.name("function"), SQLDataType.VARCHAR(100)))
                .column(DSL.field(DSL.name("created_at"), SQLDataType.TIMESTAMP.nullable(false)
                        .defaultValue(DSL.currentTimestamp())))
                .column(DSL.field(DSL.name("updated_at"), SQLDataType.TIMESTAMP))
                .column(DSL.field(DSL.name("created_by"), SQLDataType.UUID))
                .column(DSL.field(DSL.name("updated_by"), SQLDataType.UUID))
                .constraint(DSL.constraint(DSL.name("pk_game_participants")).primaryKey(DSL.field(DSL.name("id"))))
                .constraint(DSL.constraint(DSL.name("fk_game_participants_game"))
                        .foreignKey(DSL.name("game_id"))
                        .references(DSL.name("platform", "games"), DSL.name("id"))
                        .onDeleteCascade())
                .constraint(DSL.constraint(DSL.name("fk_game_participants_person"))
                        .foreignKey(DSL.name("person_id"))
                        .references(DSL.name("platform", "persons"), DSL.name("id"))
                        .onDeleteCascade())
                .constraint(DSL.constraint(DSL.name("uk_game_participants_game_person_role"))
                        .unique(DSL.name("game_id"), DSL.name("person_id"), DSL.name("role")))
                .execute();

        // Indice para buscas por jogo
        dsl.createIndexIfNotExists(DSL.name("idx_game_participants_game"))
                .on(DSL.table(DSL.name("platform", "game_participants")),
                        DSL.field(DSL.name("game_id")))
                .execute();

        // Indice para buscas por pessoa
        dsl.createIndexIfNotExists(DSL.name("idx_game_participants_person"))
                .on(DSL.table(DSL.name("platform", "game_participants")),
                        DSL.field(DSL.name("person_id")))
                .execute();
    }
}
