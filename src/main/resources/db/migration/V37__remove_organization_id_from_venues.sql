-- Remove organization_id from venues table
-- This field is no longer used by the frontend

-- Drop the unique constraint that depends on organization_id
ALTER TABLE platform.venues
    DROP CONSTRAINT IF EXISTS uk_venues_organization_name;

-- Drop the foreign key constraint
ALTER TABLE platform.venues
    DROP CONSTRAINT IF EXISTS fk_venues_organization;

-- Drop the column
ALTER TABLE platform.venues
    DROP COLUMN IF EXISTS organization_id;
