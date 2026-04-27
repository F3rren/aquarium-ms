-- CQRS read model: ultimo valore misurato per acquario (O(1) lookup)
CREATE TABLE parameters.parameter_latest (
    aquarium_id  BIGINT       PRIMARY KEY,
    temperature  DOUBLE PRECISION,
    ph           DOUBLE PRECISION,
    salinity     INTEGER,
    orp          INTEGER,
    measured_at  TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL
);

-- Idempotent consumer: eventi già processati
CREATE TABLE parameters.processed_events (
    event_id     VARCHAR(36) PRIMARY KEY,
    processed_at TIMESTAMPTZ NOT NULL
);
