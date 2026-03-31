CREATE SCHEMA IF NOT EXISTS parameters;

CREATE TABLE IF NOT EXISTS parameters.water_parameters (
    id          BIGSERIAL PRIMARY KEY,
    aquarium_id BIGINT        NOT NULL,
    temperature DOUBLE PRECISION NOT NULL,
    ph          DOUBLE PRECISION NOT NULL,
    salinity    INTEGER       NOT NULL,
    orp         INTEGER       NOT NULL,
    measured_at TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_water_parameters_aquarium_id
    ON parameters.water_parameters (aquarium_id);

CREATE INDEX IF NOT EXISTS idx_water_parameters_measured_at
    ON parameters.water_parameters (measured_at DESC);
