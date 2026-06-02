-- Drop old lowercase CHECK constraint (auto-named by PostgreSQL from V1 inline CHECK)
ALTER TABLE inhabitants.aquarium_inhabitants
    DROP CONSTRAINT IF EXISTS aquarium_inhabitants_inhabitant_type_check;

-- Normalize existing data from lowercase to uppercase to align with @Enumerated(EnumType.STRING)
UPDATE inhabitants.aquarium_inhabitants
SET inhabitant_type = UPPER(inhabitant_type)
WHERE inhabitant_type IN ('fish', 'coral');

-- Add new uppercase CHECK constraint
ALTER TABLE inhabitants.aquarium_inhabitants
    ADD CONSTRAINT aquarium_inhabitants_inhabitant_type_check
    CHECK (inhabitant_type IN ('FISH', 'CORAL'));
