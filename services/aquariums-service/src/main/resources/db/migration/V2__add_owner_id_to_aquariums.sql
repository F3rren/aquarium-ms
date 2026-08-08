-- Adds ownership tracking so authorization (not just authentication) can be enforced on a
-- per-resource basis. Backfills existing rows to 0 (an id no real gateway-issued user will ever
-- have) then drops the default, so every future insert must supply a real owner explicitly.
ALTER TABLE core.aquariums
    ADD COLUMN owner_id BIGINT NOT NULL DEFAULT 0;

ALTER TABLE core.aquariums
    ALTER COLUMN owner_id DROP DEFAULT;

CREATE INDEX IF NOT EXISTS idx_aquariums_owner_id ON core.aquariums (owner_id);
