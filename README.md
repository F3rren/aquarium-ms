# Aquarium Microservices

![Java 17](https://img.shields.io/badge/java-17-blue.svg)
![Spring Boot 3.3.5](https://img.shields.io/badge/spring--boot-3.3.5-green.svg)
![Microservices](https://img.shields.io/badge/architecture-microservices-blue.svg)
![Docker](https://img.shields.io/badge/docker-ready-blue.svg)
![PostgreSQL](https://img.shields.io/badge/database-PostgreSQL%2016-336791.svg)
![MIT License](https://img.shields.io/badge/license-MIT-green.svg)

A **microservice-based backend** for comprehensive aquarium management — tanks, inhabitants, species, maintenance tasks, and water parameters.

Built with **Java 17, Spring Boot 3.3.5, Spring Cloud Gateway** and **PostgreSQL 16**. Fully containerized via Docker Compose with Prometheus and Grafana monitoring.

> **Note:** This backend is designed to work alongside the [Aquarium Interface](https://github.com/F3rren/Aquarium-interface) frontend and is not intended as a public/general-purpose API.

---

## Quick Start

**Only prerequisite: [Docker Desktop](https://www.docker.com/products/docker-desktop/)**

```bash
git clone https://github.com/F3rren/aquarium-ms.git
cd aquarium-ms
docker-compose up -d
```

That's it. Docker Compose will build all images, start PostgreSQL, all microservices, and the monitoring stack automatically.

Wait ~30–60 seconds for services to start, then access:

| Service | URL |
|---------|-----|
| API Gateway (main entrypoint) | http://localhost:8080 |
| Swagger UI (all services) | http://localhost:8080/swagger-ui.html |
| Grafana (metrics) | http://localhost:3000 (admin / admin) |
| Prometheus | http://localhost:9090 |

### Stop

```bash
docker-compose down          # stop containers
docker-compose down -v       # stop and delete all data (volumes)
```

---

## Architecture

All HTTP traffic goes through the **API Gateway** on port 8080, which routes requests to the appropriate microservice.

```
Client → API Gateway (8080) → aquariums-service    (8081)
                             → inhabitants-service  (8082)
                             → species-service      (8083)
                             → maintenance-service  (8084)
                             → parameters-service   (8085)
                             → manual-parameters-service (8086)
                             → target-parameters-service (8087)
```

### Services

| Service | Port | Responsibility |
|---------|------|----------------|
| `api-gateway` | 8080 | Single entrypoint, path-based routing, CORS, Swagger aggregation |
| `aquariums-service` | 8081 | Aquarium CRUD (name, type, volume, etc.) |
| `inhabitants-service` | 8082 | Inhabitants assigned to each aquarium |
| `species-service` | 8083 | Shared species catalog (fish and corals) |
| `maintenance-service` | 8084 | Maintenance tasks per aquarium |
| `parameters-service` | 8085 | Water parameter readings and history |
| `manual-parameters-service` | 8086 | Manually entered measurements |
| `target-parameters-service` | 8087 | Target parameter ranges for alerts and comparison |

### Database

All services share a single **PostgreSQL 16** instance (`aquarium_ms` database) with three logical schemas:

- `core` — aquariums, maintenance
- `inhabitants` — inhabitants, species
- `parameters` — water, manual and target parameters

---

## API Endpoints

All requests go through the gateway at `http://localhost:8080`.

| Area | Method | Path |
|------|--------|------|
| Aquariums | GET, POST | `/aquariums` |
| Aquarium | GET, PUT, DELETE | `/aquariums/{id}` |
| Inhabitants | GET, POST | `/aquariums/{id}/inhabitants` |
| Inhabitant | GET, PUT, DELETE | `/aquariums/{id}/inhabitants/{inhabitantId}` |
| Species | GET, POST | `/species` |
| Species item | GET, PUT, DELETE | `/species/{id}` |
| Maintenance tasks | GET, POST | `/aquariums/{id}/tasks` |
| Maintenance task | GET, PUT, DELETE | `/aquariums/{id}/tasks/{taskId}` |
| Water parameters | GET, POST | `/aquariums/{id}/parameters` |
| Manual parameters | GET, POST | `/aquariums/{id}/parameters/manual` |
| Target parameters | GET, POST | `/aquariums/{id}/settings/targets` |

Full interactive documentation available at **http://localhost:8080/swagger-ui.html**.

---

## Observability

Prometheus scrapes metrics from all services every 15 seconds via `/actuator/prometheus`. Grafana comes pre-configured with Prometheus as datasource.

- **Grafana:** http://localhost:3000 (admin / admin) — pre-built dashboard "Aquarium Microservices - Overview"
- **Prometheus:** http://localhost:9090 — raw metrics and target status

---

## Customization

To change table names, schemas, or service configuration, edit the relevant `application.yml` inside each service under `services/<service-name>/src/main/resources/`.

To change database credentials, update both the `postgres` service and all `SPRING_DATASOURCE_*` environment variables in `docker-compose.yml`.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 |
| Gateway | Spring Cloud Gateway |
| Persistence | Spring Data JPA, Hibernate, Flyway |
| Database | PostgreSQL 16 |
| Containerization | Docker, Docker Compose |
| Metrics | Prometheus, Grafana |
| Build | Maven |
| Utilities | Lombok |

---

## Previous Version

This project is the microservice evolution of a previous monolithic backend: [aquarium-monitor](https://github.com/F3rren/aquarium-monitor).

---

## License

MIT © F3rren
