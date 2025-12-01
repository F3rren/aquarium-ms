-- Creazione degli schemi per i microservizi
CREATE SCHEMA IF NOT EXISTS core;
CREATE SCHEMA IF NOT EXISTS inhabitants;
CREATE SCHEMA IF NOT EXISTS parameters;

-- Grant dei permessi
GRANT ALL PRIVILEGES ON SCHEMA core TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA inhabitants TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA parameters TO postgres;
