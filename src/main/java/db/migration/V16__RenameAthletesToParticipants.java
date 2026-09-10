package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * Migration V16: Renomeia tabela athletes para participants.
 *
 * <p>Tabelas afetadas:
 * <ul>
 *   <li>athletes → participants</li>
 *   <li>athlete_positions → participant_positions (coluna athlete_id → participant_id)</li>
 * </ul>
 *
 * <p>Constraints e indices renomeados:
 * <ul>
 *   <li>PK athletes_pkey → participants_pkey</li>
 *   <li>UK uk_athletes_cpf → uk_participants_cpf</li>
 *   <li>FK fk_athlete_positions_athlete → participant_positions_participant_id_fkey</li>
 *   <li>FK fk_team_roster_athlete → fk_team_roster_participant</li>
 *   <li>FK fk_checkins_athlete → fk_checkins_participant</li>
 * </ul>
 *
 * <p>Ordem obrigatoria: dropar TODAS as FKs dependentes da PK antes de dropar a PK,
 * renomear colunas, depois recriar FKs com nomes e colunas atualizados.
 */
public class V16__RenameAthletesToParticipants extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        // 1. Dropar TODAS as FKs que dependem da PK athletes_pkey
        dsl.execute("ALTER TABLE platform.athlete_positions DROP CONSTRAINT IF EXISTS fk_athlete_positions_athlete");
        dsl.execute("ALTER TABLE platform.team_roster DROP CONSTRAINT IF EXISTS fk_team_roster_athlete");
        dsl.execute("ALTER TABLE platform.checkins DROP CONSTRAINT IF EXISTS fk_checkins_athlete");

        // 2. Renomear tabelas
        dsl.alterTable(DSL.name("platform", "athletes"))
                .renameTo(DSL.name("platform", "participants"))
                .execute();

        dsl.alterTable(DSL.name("platform", "athlete_positions"))
                .renameTo(DSL.name("platform", "participant_positions"))
                .execute();

        // 3. Renomear constraint PK (agora seguro pois todas as FKs ja foram dropadas)
        dsl.execute("ALTER TABLE platform.participants DROP CONSTRAINT IF EXISTS athletes_pkey");
        dsl.execute("ALTER TABLE platform.participants ADD CONSTRAINT participants_pkey PRIMARY KEY (id)");

        // 4. Renomear indice unico de CPF
        dsl.execute("DROP INDEX IF EXISTS platform.uk_athletes_cpf");
        dsl.execute("CREATE UNIQUE INDEX uk_participants_cpf ON platform.participants (cpf) WHERE cpf IS NOT NULL");

        // 5. Renomear coluna athlete_id -> participant_positions na tabela participant_positions
        dsl.execute("ALTER TABLE platform.participant_positions RENAME COLUMN athlete_id TO participant_id");

        // 6. Recriar TODAS as FKs com nomes e colunas atualizados
        dsl.execute("ALTER TABLE platform.participant_positions ADD CONSTRAINT participant_positions_participant_id_fkey FOREIGN KEY (participant_id) REFERENCES platform.participants(id)");
        dsl.execute("ALTER TABLE platform.team_roster ADD CONSTRAINT fk_team_roster_participant FOREIGN KEY (athlete_id) REFERENCES platform.participants(id)");
        dsl.execute("ALTER TABLE platform.checkins ADD CONSTRAINT fk_checkins_participant FOREIGN KEY (athlete_id) REFERENCES platform.participants(id)");
    }
}
