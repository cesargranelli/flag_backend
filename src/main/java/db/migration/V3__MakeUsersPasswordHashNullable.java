package db.migration;

import java.sql.Connection;
import java.sql.Statement;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

/**
 * Migration V3: Torna a coluna password_hash opcional na tabela platform.users,
 * conforme Seção 3 da Especificação Técnica (Autenticação Híbrida via Firebase Auth).
 */
public class V3__MakeUsersPasswordHashNullable extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE platform.users ALTER COLUMN password_hash DROP NOT NULL;");
        }
    }
}
