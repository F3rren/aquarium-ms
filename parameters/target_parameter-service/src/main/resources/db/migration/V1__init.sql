CREATE SCHEMA IF NOT EXISTS parameters;

CREATE TABLE IF NOT EXISTS parameters.target_parameters (
    id          BIGSERIAL PRIMARY KEY,
    aquarium_id BIGINT           NOT NULL UNIQUE,
    temperature DOUBLE PRECISION,
    ph          DOUBLE PRECISION,
    salinity    DOUBLE PRECISION,
    orp         DOUBLE PRECISION
);

CREATE INDEX IF NOT EXISTS idx_target_parameters_aquarium_id
    ON parameters.target_parameters (aquarium_id);
