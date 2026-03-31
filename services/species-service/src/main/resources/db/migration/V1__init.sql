CREATE SCHEMA IF NOT EXISTS inhabitants;

CREATE TABLE IF NOT EXISTS inhabitants.fish (
    id               BIGSERIAL PRIMARY KEY,
    common_name      VARCHAR(255) NOT NULL,
    scientific_name  VARCHAR(255) NOT NULL,
    family           VARCHAR(255) NOT NULL,
    min_tank_size    INTEGER      NOT NULL,
    max_size         INTEGER      NOT NULL,
    difficulty       VARCHAR(100) NOT NULL,
    reef_safe        BOOLEAN      NOT NULL DEFAULT FALSE,
    temperament      VARCHAR(100) NOT NULL,
    diet             VARCHAR(255) NOT NULL,
    image_url        VARCHAR(512),
    description      TEXT,
    water_type       VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_fish_common_name ON inhabitants.fish (common_name);

CREATE TABLE IF NOT EXISTS inhabitants.corals (
    id                BIGSERIAL PRIMARY KEY,
    common_name       VARCHAR(255) NOT NULL,
    scientific_name   VARCHAR(255) NOT NULL,
    type              VARCHAR(100) NOT NULL,
    min_tank_size     INTEGER      NOT NULL,
    max_size          INTEGER      NOT NULL,
    difficulty        VARCHAR(100) NOT NULL,
    light_requirement VARCHAR(100) NOT NULL,
    flow_requirement  VARCHAR(100) NOT NULL,
    placement         VARCHAR(100) NOT NULL,
    aggressive        BOOLEAN      NOT NULL DEFAULT FALSE,
    feeding           VARCHAR(255) NOT NULL,
    description       TEXT         NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_corals_common_name ON inhabitants.corals (common_name);
