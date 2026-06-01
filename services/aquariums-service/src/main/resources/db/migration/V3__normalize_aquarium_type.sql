-- The `type` column is now mapped to the AquariumType enum via EnumType.STRING,
-- which persists the enum name in upper case (e.g. 'SALTWATER').
-- Normalize any pre-existing lower/mixed-case values so existing rows keep mapping.
UPDATE core.aquariums
SET type = UPPER(type)
WHERE type IS NOT NULL
  AND type <> UPPER(type);
