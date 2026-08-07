CREATE TABLE IF NOT EXISTS core.aquariums (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    volume      INTEGER       NOT NULL CHECK (volume >= 1),
    type        VARCHAR(50)   NOT NULL,
    created_at  TIMESTAMPTZ   DEFAULT NOW(),
    description VARCHAR(500),
    image_url   VARCHAR(2000)
);

CREATE INDEX IF NOT EXISTS idx_aquariums_name ON core.aquariums (name);
CREATE INDEX IF NOT EXISTS idx_aquariums_type ON core.aquariums (type);
