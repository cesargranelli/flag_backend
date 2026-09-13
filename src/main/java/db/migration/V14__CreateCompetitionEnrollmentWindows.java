package db.migration;

import java.sql.Connection;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

/**
 * Migration V14: Cria a tabela platform.competition_enrollment_windows para controlar
 * a abertura e encerramento dos periodos de inscricao de equipes por competicao.
 *
 * <p>Segue o mesmo padrao de platform.affiliation_windows:
 * a organizacao promotora abre/encerra o periodo; a agremiacao so consegue
 * inscrever sua equipe enquanto a janela estiver OPEN e dentro do intervalo de datas.
 */
public class V14__CreateCompetitionEnrollmentWindows extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        dsl.createTableIfNotExists(DSL.name("platform", "competition_enrollment_windows"))
                .column(DSL.field(DSL.name("id"), SQLDataType.UUID.nullable(false)
                        .defaultValue(DSL.function("gen_random_uuid", SQLDataType.UUID))))
                .column(DSL.field(DSL.name("competition_id"), SQLDataType.UUID.nullable(false)))
                .column(DSL.field(DSL.name("title"), SQLDataType.VARCHAR(150).nullable(false)))
                .column(DSL.field(DSL.name("start_date"), SQLDataType.LOCALDATE.nullable(false)))
                .column(DSL.field(DSL.name("end_date"), SQLDataType.LOCALDATE.nullable(false)))
                .column(DSL.field(DSL.name("status"), SQLDataType.VARCHAR(20).nullable(false)
                        .defaultValue(DSL.inline("OPEN"))))
                .column(DSL.field(DSL.name("instructions"), SQLDataType.VARCHAR(1000)))
                .column(DSL.field(DSL.name("created_by_email"), SQLDataType.VARCHAR(150)))
                .column(DSL.field(DSL.name("created_at"), SQLDataType.TIMESTAMP.nullable(false)
                        .defaultValue(DSL.currentTimestamp())))
                .column(DSL.field(DSL.name("updated_at"), SQLDataType.TIMESTAMP))
                .constraint(DSL.constraint(DSL.name("pk_competition_enrollment_windows"))
                        .primaryKey(DSL.name("id")))
                .constraint(DSL.constraint(DSL.name("fk_enroll_win_competition"))
                        .foreignKey(DSL.name("competition_id"))
                        .references(DSL.name("platform", "competitions"), DSL.name("id"))
                        .onDeleteCascade())
                // Uma competicao tem no maximo uma janela de inscricao ativa por vez
                .constraint(DSL.constraint(DSL.name("uk_enroll_win_competition"))
                        .unique(DSL.name("competition_id")))
                .execute();

        dsl.createIndexIfNotExists(DSL.name("idx_enroll_win_competition_status"))
                .on(DSL.table(DSL.name("platform", "competition_enrollment_windows")),
                        DSL.field(DSL.name("competition_id")),
                        DSL.field(DSL.name("status")))
                .execute();
    }
}
