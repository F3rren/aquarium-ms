-- Normalize fish enum fields to uppercase for @Enumerated(EnumType.STRING)
UPDATE inhabitants.fish SET difficulty = UPPER(difficulty) WHERE difficulty IS NOT NULL;
UPDATE inhabitants.fish SET diet      = UPPER(diet)       WHERE diet IS NOT NULL;
UPDATE inhabitants.fish SET water_type = UPPER(water_type) WHERE water_type IS NOT NULL;

-- temperament: handle multi-word value "Semi-aggressive" before simple UPPER()
UPDATE inhabitants.fish SET temperament = 'SEMI_AGGRESSIVE' WHERE LOWER(temperament) LIKE 'semi%';
UPDATE inhabitants.fish SET temperament = 'PEACEFUL'        WHERE LOWER(temperament) = 'peaceful';
UPDATE inhabitants.fish SET temperament = 'AGGRESSIVE'      WHERE LOWER(temperament) = 'aggressive';

-- Normalize coral enum fields to uppercase
UPDATE inhabitants.corals SET difficulty        = UPPER(difficulty)        WHERE difficulty IS NOT NULL;
UPDATE inhabitants.corals SET light_requirement = UPPER(light_requirement) WHERE light_requirement IS NOT NULL;
UPDATE inhabitants.corals SET flow_requirement  = UPPER(flow_requirement)  WHERE flow_requirement IS NOT NULL;
UPDATE inhabitants.corals SET placement         = UPPER(placement)         WHERE placement IS NOT NULL;

-- feeding: handle multi-word value "No feeding" before simple UPPER()
UPDATE inhabitants.corals SET feeding = 'NO_FEEDING' WHERE LOWER(feeding) LIKE 'no%';
UPDATE inhabitants.corals SET feeding = 'OCCASIONAL' WHERE LOWER(feeding) = 'occasional';
UPDATE inhabitants.corals SET feeding = 'REGULAR'    WHERE LOWER(feeding) = 'regular';

-- Add CHECK constraints on fish
ALTER TABLE inhabitants.fish
    ADD CONSTRAINT fish_difficulty_check
        CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    ADD CONSTRAINT fish_temperament_check
        CHECK (temperament IN ('PEACEFUL', 'SEMI_AGGRESSIVE', 'AGGRESSIVE')),
    ADD CONSTRAINT fish_diet_check
        CHECK (diet IN ('OMNIVORE', 'HERBIVORE', 'CARNIVORE')),
    ADD CONSTRAINT fish_water_type_check
        CHECK (water_type IS NULL OR water_type IN ('MARINE', 'FRESHWATER', 'BRACKISH'));

-- Add CHECK constraints on corals
ALTER TABLE inhabitants.corals
    ADD CONSTRAINT corals_difficulty_check
        CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    ADD CONSTRAINT corals_light_req_check
        CHECK (light_requirement IN ('LOW', 'MEDIUM', 'HIGH')),
    ADD CONSTRAINT corals_flow_req_check
        CHECK (flow_requirement IN ('LOW', 'MEDIUM', 'HIGH')),
    ADD CONSTRAINT corals_placement_check
        CHECK (placement IN ('BOTTOM', 'MIDDLE', 'TOP')),
    ADD CONSTRAINT corals_feeding_check
        CHECK (feeding IN ('NO_FEEDING', 'OCCASIONAL', 'REGULAR'));
