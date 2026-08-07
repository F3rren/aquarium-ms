-- Indexes for common filter combinations on species lookup endpoints
CREATE INDEX IF NOT EXISTS idx_fish_reef_safe  ON inhabitants.fish (reef_safe);
CREATE INDEX IF NOT EXISTS idx_fish_difficulty ON inhabitants.fish (difficulty);
CREATE INDEX IF NOT EXISTS idx_fish_water_type ON inhabitants.fish (water_type);

CREATE INDEX IF NOT EXISTS idx_corals_difficulty ON inhabitants.corals (difficulty);
CREATE INDEX IF NOT EXISTS idx_corals_aggressive ON inhabitants.corals (aggressive);
