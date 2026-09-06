package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

public class V5__CreateInstitutionsAndInstitutionOrganizations extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS platform.institutions (
                        id uuid PRIMARY KEY,
                        name text NOT NULL,
                        type text CHECK (type IN ('CLUB','UNIVERSITY')),
                        colors text[],
                        status text NOT NULL,
                        created_at timestamptz NOT NULL DEFAULT now(),
                        updated_at timestamptz NOT NULL DEFAULT now()
                    );
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS platform.institution_organizations (
                        institution_id uuid NOT NULL REFERENCES platform.institutions(id) ON DELETE CASCADE,
                        organization_id uuid NOT NULL REFERENCES platform.organizations(id) ON DELETE CASCADE,
                        created_at timestamptz NOT NULL DEFAULT now(),
                        created_by uuid,
                        PRIMARY KEY (institution_id, organization_id)
                    );
                    """);

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_institution_organizations_organization_id ON platform.institution_organizations (organization_id);");
        }
    }
}
