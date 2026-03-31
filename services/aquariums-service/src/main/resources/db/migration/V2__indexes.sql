-- Indexes to speed up queries on frequently filtered/sorted columns
CREATE INDEX IF NOT EXISTS idx_aquariums_name ON core.aquariums (name);
CREATE INDEX IF NOT EXISTS idx_aquariums_type ON core.aquariums (type);
