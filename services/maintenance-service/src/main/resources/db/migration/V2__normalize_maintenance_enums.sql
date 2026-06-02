-- Normalize existing lowercase values to uppercase for @Enumerated(EnumType.STRING)
UPDATE maintenance.maintenance_tasks
SET frequency = UPPER(frequency)
WHERE frequency IS NOT NULL;

UPDATE maintenance.maintenance_tasks
SET priority = UPPER(priority)
WHERE priority IS NOT NULL;

-- Add CHECK constraints (fields are nullable, so allow NULL)
ALTER TABLE maintenance.maintenance_tasks
    ADD CONSTRAINT maintenance_tasks_frequency_check
    CHECK (frequency IS NULL OR frequency IN ('DAILY', 'WEEKLY', 'MONTHLY', 'CUSTOM'));

ALTER TABLE maintenance.maintenance_tasks
    ADD CONSTRAINT maintenance_tasks_priority_check
    CHECK (priority IS NULL OR priority IN ('LOW', 'MEDIUM', 'HIGH'));
