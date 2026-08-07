CREATE SCHEMA IF NOT EXISTS inhabitants;

CREATE TABLE IF NOT EXISTS inhabitants.fish (
    id              BIGSERIAL PRIMARY KEY,
    common_name     VARCHAR(255) NOT NULL,
    scientific_name VARCHAR(255) NOT NULL,
    family          VARCHAR(255) NOT NULL,
    min_tank_size   INTEGER      NOT NULL,
    max_size        INTEGER      NOT NULL,
    difficulty      VARCHAR(100) NOT NULL CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    reef_safe       BOOLEAN      NOT NULL DEFAULT FALSE,
    temperament     VARCHAR(100) NOT NULL CHECK (temperament IN ('PEACEFUL', 'SEMI_AGGRESSIVE', 'AGGRESSIVE')),
    diet            VARCHAR(255) NOT NULL CHECK (diet IN ('OMNIVORE', 'HERBIVORE', 'CARNIVORE')),
    image_url       VARCHAR(512),
    description     TEXT,
    water_type      VARCHAR(100) CHECK (water_type IS NULL OR water_type IN ('MARINE', 'FRESHWATER', 'BRACKISH'))
);

CREATE INDEX IF NOT EXISTS idx_fish_common_name ON inhabitants.fish (common_name);
CREATE INDEX IF NOT EXISTS idx_fish_reef_safe   ON inhabitants.fish (reef_safe);
CREATE INDEX IF NOT EXISTS idx_fish_difficulty  ON inhabitants.fish (difficulty);
CREATE INDEX IF NOT EXISTS idx_fish_water_type  ON inhabitants.fish (water_type);

CREATE TABLE IF NOT EXISTS inhabitants.corals (
    id                BIGSERIAL PRIMARY KEY,
    common_name       VARCHAR(255) NOT NULL,
    scientific_name   VARCHAR(255) NOT NULL,
    type              VARCHAR(100) NOT NULL,
    min_tank_size     INTEGER      NOT NULL,
    max_size          INTEGER      NOT NULL,
    difficulty        VARCHAR(100) NOT NULL CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    light_requirement VARCHAR(100) NOT NULL CHECK (light_requirement IN ('LOW', 'MEDIUM', 'HIGH')),
    flow_requirement  VARCHAR(100) NOT NULL CHECK (flow_requirement IN ('LOW', 'MEDIUM', 'HIGH')),
    placement         VARCHAR(100) NOT NULL CHECK (placement IN ('BOTTOM', 'MIDDLE', 'TOP')),
    aggressive        BOOLEAN      NOT NULL DEFAULT FALSE,
    feeding           VARCHAR(255) NOT NULL CHECK (feeding IN ('NO_FEEDING', 'OCCASIONAL', 'REGULAR')),
    description       TEXT         NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_corals_common_name ON inhabitants.corals (common_name);
CREATE INDEX IF NOT EXISTS idx_corals_difficulty  ON inhabitants.corals (difficulty);
CREATE INDEX IF NOT EXISTS idx_corals_aggressive  ON inhabitants.corals (aggressive);
