-- Adds the free-text verification/moderation marker (see Aquarium#verified) - nullable, no
-- default, since it's meant to be set later by a staff review, not at creation time.
ALTER TABLE core.aquariums
    ADD COLUMN verified VARCHAR(100);
