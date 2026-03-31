CREATE SCHEMA IF NOT EXISTS parameters;

CREATE TABLE IF NOT EXISTS parameters.manual_parameters (
    id          BIGSERIAL PRIMARY KEY,
    aquarium_id BIGINT           NOT NULL,
    calcium     DOUBLE PRECISION,
    magnesium   DOUBLE PRECISION,
    kh          DOUBLE PRECISION,
    nitrate     DOUBLE PRECISION,
    phosphate   DOUBLE PRECISION,
    notes       VARCHAR(2000),
    measured_at TIMESTAMP        NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_manual_parameters_aquarium_id
    ON parameters.manual_parameters (aquarium_id);

CREATE INDEX IF NOT EXISTS idx_manual_parameters_measured_at
    ON parameters.manual_parameters (measured_at DESC);
