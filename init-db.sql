-- Schemas for aquarium microservices
-- Each service uses its own tables within the shared aquarium_ms database

CREATE SCHEMA IF NOT EXISTS core;
CREATE SCHEMA IF NOT EXISTS inhabitants;
CREATE SCHEMA IF NOT EXISTS parameters;

GRANT ALL PRIVILEGES ON SCHEMA core TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA inhabitants TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA parameters TO postgres;
