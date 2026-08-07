-- Fix monetary column: DOUBLE PRECISION loses precision for currency values
ALTER TABLE maintenance.products
    ALTER COLUMN cost       TYPE NUMERIC(10,2) USING ROUND(cost::NUMERIC, 2),
    ALTER COLUMN created_at TYPE TIMESTAMPTZ   USING created_at AT TIME ZONE 'UTC',
    ALTER COLUMN updated_at TYPE TIMESTAMPTZ   USING updated_at AT TIME ZONE 'UTC';

-- Fix TIMESTAMP → TIMESTAMPTZ on maintenance_tasks
ALTER TABLE maintenance.maintenance_tasks
    ALTER COLUMN due_date     TYPE TIMESTAMPTZ USING due_date     AT TIME ZONE 'UTC',
    ALTER COLUMN completed_at TYPE TIMESTAMPTZ USING completed_at AT TIME ZONE 'UTC',
    ALTER COLUMN created_at   TYPE TIMESTAMPTZ USING created_at   AT TIME ZONE 'UTC';
