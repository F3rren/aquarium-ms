CREATE SCHEMA IF NOT EXISTS inhabitants;

CREATE TABLE IF NOT EXISTS inhabitants.aquarium_inhabitants (
    id                  BIGSERIAL PRIMARY KEY,
    aquarium_id         BIGINT        NOT NULL,
    inhabitant_type     VARCHAR(10)   NOT NULL CHECK (inhabitant_type IN ('fish', 'coral')),
    inhabitant_id       BIGINT        NOT NULL,
    quantity            INTEGER       DEFAULT 1,
    added_date          TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    notes               VARCHAR(1000),
    custom_name         VARCHAR(255),
    current_size        INTEGER,
    custom_difficulty   VARCHAR(50),
    custom_temperament  VARCHAR(50),
    custom_diet         VARCHAR(500),
    is_reef_safe        BOOLEAN,
    custom_min_tank_size INTEGER
);

CREATE INDEX IF NOT EXISTS idx_inhabitants_aquarium_id ON inhabitants.aquarium_inhabitants(aquarium_id);
CREATE INDEX IF NOT EXISTS idx_inhabitants_type        ON inhabitants.aquarium_inhabitants(inhabitant_type);
