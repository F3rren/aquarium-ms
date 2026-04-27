CREATE TABLE core.outbox_events (
    id           VARCHAR(36)  PRIMARY KEY,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type   VARCHAR(100) NOT NULL,
    payload      TEXT         NOT NULL,
    topic        VARCHAR(255) NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL,
    published_at TIMESTAMPTZ
);

CREATE INDEX idx_outbox_unpublished ON core.outbox_events (created_at)
    WHERE published_at IS NULL;
