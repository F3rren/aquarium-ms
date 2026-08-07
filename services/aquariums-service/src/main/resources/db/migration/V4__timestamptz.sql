-- TIMESTAMP → TIMESTAMPTZ: store timezone-aware values to prevent DST misinterpretation
ALTER TABLE core.aquariums
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC';
