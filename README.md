# Aquarium Microservices 🐠

Sistema di microservizi per la gestione di acquari con Spring Boot e PostgreSQL.

## Architettura

L'applicazione è composta da 8 servizi:

### Gateway (porta 8080)
- **api-gateway**: Punto di ingresso unico per tutti i microservizi

### Core Services
- **aquariums-service** (8081): Gestione acquari
- **maintenance-service** (8084): Gestione manutenzioni

### Inhabitants Services
- **inhabitants-service** (8082): Gestione abitanti degli acquari
- **species-service** (8083): Gestione specie (pesci e coralli)

### Parameters Services
- **parameters-service** (8085): Parametri dell'acqua
- **manual-parameters-service** (8086): Parametri manuali
- **target-parameters-service** (8087): Parametri target

### Database
- **PostgreSQL** (5432): Database con 3 schemi (core, inhabitants, parameters)

## 🚀 Avvio con Docker

### Prerequisiti
- Docker Desktop installato e in esecuzione
- Docker Compose

### Build e avvio completo

```bash
# Build di tutti i servizi
docker-compose build

# Avvio di tutti i servizi
docker-compose up -d

# Visualizza i log
docker-compose logs -f

# Visualizza lo stato
docker-compose ps
```

### Comandi utili

```bash
# Ferma tutti i servizi
docker-compose down

# Rimuovi anche i volumi (attenzione: cancella i dati!)
docker-compose down -v

# Riavvia un singolo servizio
docker-compose restart api-gateway

# Rebuild di un singolo servizio
docker-compose build aquariums-service
docker-compose up -d aquariums-service

# Visualizza i log di un servizio specifico
docker-compose logs -f api-gateway
```

## 📡 Endpoints

Tutti gli endpoint sono accessibili tramite il gateway su `http://localhost:8080`:

### Acquari
- `GET/POST http://localhost:8080/api/aquariums`
- `GET/PUT/DELETE http://localhost:8080/api/aquariums/{id}`

### Abitanti
- `GET/POST http://localhost:8080/api/aquariums/{id}/inhabitants`
- `GET/PUT/DELETE http://localhost:8080/api/aquariums/{aquariumId}/inhabitants/{id}`

### Specie
- `GET/POST http://localhost:8080/api/species/fishs`
- `GET/PUT/DELETE http://localhost:8080/api/species/fishs/{id}`
- `GET/POST http://localhost:8080/api/species/corals`
- `GET/PUT/DELETE http://localhost:8080/api/species/corals/{id}`

### Manutenzione
- `GET/POST http://localhost:8080/api/aquariums/{id}/maintenance`
- `GET/PUT/DELETE http://localhost:8080/api/aquariums/{aquariumId}/maintenance/{id}`

### Parametri
- `GET/POST http://localhost:8080/api/water-parameters`
- `GET/POST http://localhost:8080/api/manual-parameters`
- `GET/POST http://localhost:8080/api/target-parameters`

## 🛠️ Sviluppo locale (senza Docker)

### Prerequisiti
- Java 21
- Maven 3.x
- PostgreSQL 16

### Setup database

```sql
CREATE DATABASE aquarium_ms;
CREATE SCHEMA core;
CREATE SCHEMA inhabitants;
CREATE SCHEMA parameters;
```

### Avvio servizi

In terminali separati:

```bash
# Gateway
cd gateway/api-gateway
./mvnw spring-boot:run

# Core
cd core/aquariums-service
./mvnw spring-boot:run

# Inhabitants
cd inhabitants/inhabitants-service
./mvnw spring-boot:run

cd inhabitants/species-service
./mvnw spring-boot:run

# Maintenance
cd maintenance/maintenance-service
./mvnw spring-boot:run

# Parameters
cd parameters/parameters-service
./mvnw spring-boot:run

cd parameters/manual_parameters-service
./mvnw spring-boot:run

cd parameters/target_parameter-service
./mvnw spring-boot:run
```

## 🔧 Tecnologie

- **Spring Boot 4.0.0**
- **Spring Cloud Gateway**
- **Spring Data JPA**
- **PostgreSQL 16**
- **Docker & Docker Compose**
- **Java 21**
- **Lombok**

## 📊 Monitoraggio

Accedi ai singoli servizi direttamente (utile per debug):

- Gateway: http://localhost:8080
- Aquariums: http://localhost:8081
- Inhabitants: http://localhost:8082
- Species: http://localhost:8083
- Maintenance: http://localhost:8084
- Water Parameters: http://localhost:8085
- Manual Parameters: http://localhost:8086
- Target Parameters: http://localhost:8087
- PostgreSQL: localhost:5432

## 📝 Note

- Il gateway usa routing basato su path matching
- CORS è abilitato globalmente sul gateway
- Il database PostgreSQL usa schemi separati per organizzare le tabelle
- Tutti i servizi usano Hibernate con `ddl-auto=update`
