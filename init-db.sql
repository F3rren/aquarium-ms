-- Creation of schemas for the microservices
CREATE SCHEMA IF NOT EXISTS core;
CREATE SCHEMA IF NOT EXISTS inhabitants;
CREATE SCHEMA IF NOT EXISTS parameters;

-- Granting permissions
GRANT ALL PRIVILEGES ON SCHEMA core TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA inhabitants TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA parameters TO postgres;
