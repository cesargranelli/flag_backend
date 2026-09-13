package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.Connection;

/**
 * Migration V20: Rename user roles to align with updated UserRole enum.
 *
 * <p>Mapeamento:
 * <ul>
 *   <li>ADMIN_INSTITUTION → ORGANIZER</li>
 *   <li>CLUB_MANAGER → MANAGER</li>
 *   <li>MESA → COMMISSIONER (se existir)</li>
 *   <li>ADMIN_LIGA → ORGANIZER (se existir)</li>
 * </ul>
 *
 * <p>As roles ADMIN, ORGANIZER, REFEREE, COMMISSIONER e FAN permanecem inalteradas.</p>
 */
public class V20__RenameUserRoles extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);

        // Renomear ADMIN_INSTITUTION → ORGANIZER
        dsl.execute("UPDATE platform.users SET role = 'ORGANIZER' WHERE role = 'ADMIN_INSTITUTION'");

        // Renomear CLUB_MANAGER → MANAGER
        dsl.execute("UPDATE platform.users SET role = 'MANAGER' WHERE role = 'CLUB_MANAGER'");

        // Renomear MESA → COMMISSIONER (caso existam registros legados)
        dsl.execute("UPDATE platform.users SET role = 'COMMISSIONER' WHERE role = 'MESA'");

        // Renomear ADMIN_LIGA → ORGANIZER (caso existam registros legados)
        dsl.execute("UPDATE platform.users SET role = 'ORGANIZER' WHERE role = 'ADMIN_LIGA'");
    }
}
