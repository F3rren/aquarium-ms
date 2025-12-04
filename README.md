# Aquarium Microservices

![Java 21](https://img.shields.io/badge/java-21-blue.svg)
![Spring Boot 3.x](https://img.shields.io/badge/spring--boot-3.x-green.svg)
![Microservices](https://img.shields.io/badge/architecture-microservices-blue.svg)
![Dockerized](https://img.shields.io/badge/docker-ready-blue.svg)
![PostgreSQL](https://img.shields.io/badge/database-PostgreSQL-336791.svg)
![MIT License](https://img.shields.io/badge/license-MIT-green.svg)

A **microservice-based backend** for comprehensive aquarium management—tanks, inhabitants, species, maintenance tasks, and water parameters.  
**Built with Java 21, Spring Boot, Spring Cloud Gateway, Hibernate and PostgreSQL 16. Fully dockerized via Docker Compose to run all services and the database together.**

---

> **Note:** This backend is designed to work alongside the [Aquarium Interface](https://github.com/F3rren/Aquarium-interface) frontend.  
> *It is not intended as a public/general-purpose open API.*

---

## Project Status

- **Architecture:** 8 Spring Boot microservices + API Gateway + PostgreSQL (3 schemas: `core`, `inhabitants`, `parameters`).  
- **Use case:** Single-user/private usage for now (no authentication yet).  
- **Frontend:** Designed to be consumed by a Flutter UI (mobile/desktop/web).  
- **Evolution:** Refactored from the previous monolithic project [`aquarium-monitor`](https://github.com/F3rren/aquarium-monitor) into domain-focused microservices.  
- **Stack:** Java 21, Spring Boot 3.x, Spring Cloud Gateway, Docker, PostgreSQL 16.

---

## Key Features

- **Aquarium Management:** CRUD operations on tanks (name, type, volume, etc.).  
- **Inhabitants & Species:** Link inhabitants to tanks and manage a reusable catalog of fish and corals.  
- **Water Parameters:** Store, query and compare water parameters across time and against target ranges.  
- **Maintenance:** Schedule, track and review maintenance tasks per aquarium.  
- **Microservice architecture:** Clear separation into core, inhabitants and parameters domains, exposed via an API Gateway.  
- **Database isolation:** PostgreSQL schemas per domain to keep boundaries explicit and maintainable. [attached_file:2]

---

## Frontend & Usage

This backend is designed to be consumed primarily by a **Flutter-based frontend** that offers a cross‑platform interface (desktop/mobile) for aquarium monitoring and control.  
All HTTP traffic goes through the **API Gateway**, which exposes a single REST entrypoint suitable for web, mobile or desktop clients.

---

## Architecture

The system is split into multiple microservices, each responsible for a specific domain, exposed through a single API Gateway.

### Gateway (port 8080)

- `api-gateway`: Single entrypoint for all microservices, with path‑based routing and centralized CORS configuration.

### Core Services

- `aquariums-service` (8081): Aquarium management (CRUD, core tank data).  
- `maintenance-service` (8084): Maintenance tasks per aquarium (scheduling and history).

### Inhabitants Services

- `inhabitants-service` (8082): Inhabitants assigned to each aquarium.  
- `species-service` (8083): Shared species catalog (fish and corals).

### Parameters Services

- `parameters-service` (8085): Water parameter readings and history.  
- `manual-parameters-service` (8086): Manually entered measurements.  
- `target-parameters-service` (8087): Target parameter ranges for comparison and alerts.

### Database

- **PostgreSQL 16** (5432) with 3 logical schemas:  
  - `core`: aquariums, maintenance.  
  - `inhabitants`: inhabitants and species.  
  - `parameters`: water, manual and target parameters.

### Design & Technical Decisions

- Each domain group (`core`, `inhabitants`, `parameters`) uses its own PostgreSQL schema to keep boundaries clear.  
- The API Gateway exposes a unified HTTP surface, simplifying integration for web/mobile clients.  
- All services are stateless and use Spring Data JPA with Hibernate, with `ddl-auto=update` for fast local development (a migration tool should replace this in a real production setup). [attached_file:2]

---

## Quick Start (Docker)

### Prerequisites

- Docker Desktop running  
- Docker Compose installed


### Build and Run All Services


```bash
#Build all services
docker-compose build

#Start all services
docker-compose up -d

#Tail logs
docker-compose logs -f

#Check running containers
docker-compose ps
```

### Useful Commands

```bash
#Stop all services
docker-compose down

#Stop and remove volumes (WARNING: deletes data)
docker-compose down -v

#Restart a single service
docker-compose restart api-gateway

#Rebuild a single service
docker-compose build aquariums-service
docker-compose up -d aquariums-service

#Logs for a specific service
docker-compose logs -f api-gateway
```

---

## Main API Endpoints (via Gateway `http://localhost:8080`)

| Area              | Method & Path                                                                 |
|-------------------|-------------------------------------------------------------------------------|
| Aquariums         | GET/POST `/api/aquariums`, GET/PUT/DELETE `/api/aquariums/{id}`              |
| Water Parameters  | GET/POST `/api/water-parameters`                                             |
| Maintenance Tasks | GET/POST `/api/aquariums/{id}/maintenance`, PUT/DELETE `/api/aquariums/{aquariumId}/maintenance/{id}` |
| Inhabitants       | GET/POST `/api/aquariums/{id}/inhabitants`, GET/PUT/DELETE `/api/aquariums/{aquariumId}/inhabitants/{id}` |
| Species           | GET/POST `/api/species/fishs`, `/api/species/corals`, GET/PUT/DELETE `/api/species/fishs/{id}`, `/api/species/corals/{id}` |

---

## 🛠️ Local Development (without Docker)

### Prerequisites

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

### Run Services from Source

In separate terminals:

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
---

## API Documentation (Swagger / OpenAPI)

**All API documentation is centralized through the API Gateway:**

- **Unified Swagger UI:** http://localhost:8080/swagger-ui.html

The gateway aggregates OpenAPI documentation from all microservices into a single interface. Use the dropdown menu in the Swagger UI to switch between different services:

- `aquariums-service`: Aquarium management  
- `inhabitants-service`: Inhabitants management  
- `species-service`: Species catalog  
- `maintenance-service`: Maintenance tasks  
- `parameters-service`: Water parameters  
- `manual-parameters-service`: Manual measurements  
- `target-parameters-service`: Target ranges  

This centralized approach:
- Eliminates CORS issues  
- Provides a single entrypoint for API exploration  
- Mirrors the production routing through the gateway  
- Simplifies API testing and documentation access

> **Note:** Individual services still expose their own Swagger UIs on their respective ports (8081-8087) for development purposes, but the recommended approach is to use the centralized gateway interface.

---

## Tech Stack

- Java 21  
- Spring Boot 3.x  
- Spring Cloud Gateway  
- Spring Data JPA (Hibernate)  
- PostgreSQL 16  
- Docker & Docker Compose  
- Lombok

---

## Monitoring & Ports

Direct access to services (useful for debugging during development):

- Gateway: http://localhost:8080  
- Aquariums: http://localhost:8081  
- Inhabitants: http://localhost:8082  
- Species: http://localhost:8083  
- Maintenance: http://localhost:8084  
- Water Parameters: http://localhost:8085  
- Manual Parameters: http://localhost:8086  
- Target Parameters: http://localhost:8087  
- PostgreSQL: `localhost:5432`

---

## Previous Monolithic Version

This project is the **microservice evolution** of a previous monolithic backend:

- Previous version (monolith): https://github.com/F3rren/aquarium-monitor  

The original project was built as a single Spring Boot application with PostgreSQL and Docker, then refactored into multiple domain‑focused microservices to improve modularity, scalability, and long‑term maintainability.

---

## Roadmap

- [ ] Add authentication and multi-user support  
- [ ] Centralized logging and observability (e.g. Prometheus/Grafana)  
- [X] Hardware integration for real sensor readings (temperature, salinity, etc.)  
- [x] API documentation consolidation at gateway level (single Swagger entrypoint)
- [x] Enhanced error handling & validation
- [x] Automated API tests & integration with frontend
---

## License

MIT © F3rren