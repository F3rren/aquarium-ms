CREATE SCHEMA IF NOT EXISTS maintenance;

CREATE TABLE IF NOT EXISTS maintenance.maintenance_tasks (
    id           BIGSERIAL    PRIMARY KEY,
    aquarium_id  BIGINT       NOT NULL,
    title        VARCHAR(255) NOT NULL,
    description  VARCHAR(2000),
    frequency    VARCHAR(50)  CHECK (frequency IS NULL OR frequency IN ('DAILY', 'WEEKLY', 'MONTHLY', 'CUSTOM')),
    priority     VARCHAR(50)  CHECK (priority IS NULL OR priority IN ('LOW', 'MEDIUM', 'HIGH')),
    is_completed BOOLEAN      NOT NULL DEFAULT FALSE,
    due_date     TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    notes        VARCHAR(2000)
);

CREATE INDEX IF NOT EXISTS idx_maintenance_tasks_aquarium_id ON maintenance.maintenance_tasks (aquarium_id);
CREATE INDEX IF NOT EXISTS idx_maintenance_tasks_due_date    ON maintenance.maintenance_tasks (due_date);

CREATE TABLE IF NOT EXISTS maintenance.products (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(255)    NOT NULL,
    category        VARCHAR(50)     NOT NULL,
    brand           VARCHAR(255),
    quantity        DOUBLE PRECISION,
    unit            VARCHAR(50),
    cost            NUMERIC(10,2),
    currency        VARCHAR(10)     DEFAULT '€',
    purchase_date   DATE,
    expiry_date     DATE,
    notes           TEXT,
    image_url       VARCHAR(500),
    is_favorite     BOOLEAN         NOT NULL DEFAULT FALSE,
    usage_frequency INTEGER,
    last_used       DATE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_products_category    ON maintenance.products (category);
CREATE INDEX IF NOT EXISTS idx_products_name        ON maintenance.products (name);
CREATE INDEX IF NOT EXISTS idx_products_expiry_date ON maintenance.products (expiry_date);
