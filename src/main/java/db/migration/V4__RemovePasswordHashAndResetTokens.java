package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * Migration V4: Remove coluna password_hash e tabela password_reset_tokens.
 * <p>
 * A autenticação agora é feita exclusivamente via Firebase Auth SDK no frontend.
 * O backend não armazena mais hashes de senha nem tokens de redefinição.
 */
public class V4__RemovePasswordHashAndResetTokens extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement stmt = context.getConnection().createStatement()) {
            // Remove tabela de tokens de redefinição de senha
            stmt.execute("DROP TABLE IF EXISTS platform.password_reset_tokens CASCADE;");

            // Remove coluna password_hash da tabela users
            stmt.execute("ALTER TABLE platform.users DROP COLUMN IF EXISTS password_hash;");
        }
    }

}
