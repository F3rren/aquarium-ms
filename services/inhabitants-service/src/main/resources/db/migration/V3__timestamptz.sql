-- TIMESTAMP → TIMESTAMPTZ: store timezone-aware values to prevent DST misinterpretation
ALTER TABLE inhabitants.aquarium_inhabitants
    ALTER COLUMN added_date TYPE TIMESTAMPTZ USING added_date AT TIME ZONE 'UTC';
