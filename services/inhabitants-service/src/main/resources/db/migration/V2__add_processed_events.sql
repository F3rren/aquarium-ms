CREATE TABLE inhabitants.processed_events (
    event_id     VARCHAR(36) PRIMARY KEY,
    processed_at TIMESTAMPTZ NOT NULL
);
