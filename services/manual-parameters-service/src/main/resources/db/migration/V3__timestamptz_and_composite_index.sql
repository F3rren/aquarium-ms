-- TIMESTAMP → TIMESTAMPTZ: store timezone-aware values to prevent DST misinterpretation
ALTER TABLE parameters.manual_parameters
    ALTER COLUMN measured_at TYPE TIMESTAMPTZ USING measured_at AT TIME ZONE 'UTC';

-- Composite index matches the dominant query: latest measurements per aquarium
-- Replaces the need to use two separate indexes (aquarium_id, measured_at)
CREATE INDEX IF NOT EXISTS idx_manual_parameters_aquarium_measured
    ON parameters.manual_parameters (aquarium_id, measured_at DESC);
