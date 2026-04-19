# Aquarium Microservices

![Java 17](https://img.shields.io/badge/java-17-blue.svg)
![Spring Boot 3.3.5](https://img.shields.io/badge/spring--boot-3.3.5-green.svg)
![Microservices](https://img.shields.io/badge/architecture-microservices-blue.svg)
![Docker](https://img.shields.io/badge/docker-ready-blue.svg)
![PostgreSQL](https://img.shields.io/badge/database-PostgreSQL%2016-336791.svg)
![MIT License](https://img.shields.io/badge/license-MIT-green.svg)

A **microservice-based backend** for comprehensive aquarium management — tanks, inhabitants, species catalog, maintenance tracking, and water parameter monitoring.

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

Docker Compose will build all images and start PostgreSQL, all microservices, and the monitoring stack automatically. Wait ~30–60 seconds, then access:

| Service | URL |
|---------|-----|
| API Gateway (main entrypoint) | http://localhost:8080 |
| Swagger UI (all services) | http://localhost:8080/swagger-ui.html |
| Grafana (metrics) | http://localhost:3000 (admin / admin) |
| Prometheus | http://localhost:9090 |

```bash
docker-compose down        # stop containers
docker-compose down -v     # stop and delete all data (volumes)
```

---

## Architecture

All HTTP traffic enters through the **API Gateway** on port 8080, which routes each request to the appropriate downstream microservice based on path prefix.

```
                        ┌─────────────────────────────────────┐
                        │         API Gateway :8080           │
                        │  (routing · CORS · request logging) │
                        └──────────────┬──────────────────────┘
                                       │
          ┌──────────────┬─────────────┼──────────────┬───────────────┐
          ▼              ▼             ▼              ▼               ▼
  aquariums-service  inhabitants-  species-      maintenance-   parameters-
      :8081           service       service        service        service
                       :8082         :8083          :8084          :8085
                          │            ▲
                          └────────────┘         manual-parameters-service :8086
                     (inter-service call)
                                                 target-parameter-service  :8087
```

**Inter-service communication:**
- `inhabitants-service` → `species-service`: fetches fish/coral details to enrich inhabitant responses
- `aquariums-service` → `parameters-service`, `manual-parameters-service`, `target-parameter-service`: proxies parameter endpoints (with circuit breaker + retry via Resilience4j)

### Services

| Service | Port | Schema | Responsibility |
|---------|------|--------|----------------|
| `api-gateway` | 8080 | — | Single entrypoint, path-based routing, CORS, request/response logging |
| `aquariums-service` | 8081 | `core` | Aquarium CRUD + parameter proxy with circuit breaker |
| `inhabitants-service` | 8082 | `inhabitants` | Inhabitants per aquarium, enriched with species details |
| `species-service` | 8083 | `inhabitants` | Read-only catalog of fish and coral species |
| `maintenance-service` | 8084 | `maintenance` | Maintenance tasks and product inventory per aquarium |
| `parameters-service` | 8085 | `parameters` | Automated water parameter readings and history |
| `manual-parameters-service` | 8086 | `parameters` | Manual chemical measurements (Ca, Mg, KH, etc.) |
| `target-parameter-service` | 8087 | `parameters` | Target parameter ranges for alerts and comparison |

### Database

All services share a single **PostgreSQL 16** instance (`aquarium_ms` database) with three logical schemas:

| Schema | Tables |
|--------|--------|
| `core` | `aquariums`, maintenance tables |
| `inhabitants` | `inhabitants`, `fish`, `corals` |
| `parameters` | `parameters`, `manual_parameters`, `target_parameters` |

Schema isolation is enforced at the application level via `spring.jpa.properties.hibernate.default_schema`. Migrations are managed by **Flyway** (each service has its own migration history table to avoid conflicts).

---

## API Endpoints

All requests go through the gateway at `http://localhost:8080`. Full interactive documentation is at **http://localhost:8080/swagger-ui.html**.

### Aquariums

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/aquariums` | List all aquariums (paginated) |
| `GET` | `/aquariums/{id}` | Get aquarium by ID |
| `POST` | `/aquariums` | Create a new aquarium |
| `PUT` | `/aquariums/{id}` | Update an aquarium |
| `DELETE` | `/aquariums/{id}` | Delete an aquarium |

### Inhabitants

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/aquariums/{id}/inhabitants` | List inhabitants of an aquarium (with species details) |
| `POST` | `/aquariums/{id}/inhabitants` | Add an inhabitant |
| `PUT` | `/aquariums/{id}/inhabitants/{inhabitantId}` | Update an inhabitant |
| `DELETE` | `/aquariums/{id}/inhabitants/{inhabitantId}` | Remove an inhabitant |

### Species

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/species/fish` | List all fish species |
| `GET` | `/species/fish/{id}` | Get fish by ID |
| `GET` | `/species/corals` | List all coral species |
| `GET` | `/species/corals/{id}` | Get coral by ID |

### Maintenance Tasks

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/aquariums/{id}/tasks` | List tasks (optional `?status=pending\|completed`) |
| `POST` | `/aquariums/{id}/tasks` | Create a task |
| `PUT` | `/aquariums/{id}/tasks/{taskId}` | Update a task |
| `DELETE` | `/aquariums/{id}/tasks/{taskId}` | Delete a task |
| `POST` | `/aquariums/{id}/tasks/{taskId}/complete` | Mark a task as completed |

### Products

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/products` | List products (supports filters) |
| `GET` | `/products/categories` | List distinct product categories |
| `GET` | `/products/{id}` | Get product by ID |
| `POST` | `/products` | Create a product |
| `PUT` | `/products/{id}` | Update a product |
| `PATCH` | `/products/{id}/mark-used` | Record last use date |
| `PATCH` | `/products/{id}/toggle-favorite` | Toggle favorite flag |
| `PATCH` | `/products/{id}/quantity` | Update quantity |
| `DELETE` | `/products/{id}` | Delete a product |

### Water Parameters

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/aquariums/{id}/parameters` | List readings (optional `?limit=N`) |
| `POST` | `/aquariums/{id}/parameters` | Add a reading |
| `GET` | `/aquariums/{id}/parameters/latest` | Get the latest reading |
| `GET` | `/aquariums/{id}/parameters/history` | History by `?period=day\|week\|month` or `?from=...&to=...` |

### Manual Parameters

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/aquariums/{id}/parameters/manual` | List manual readings (optional `?limit=N`) |
| `POST` | `/aquariums/{id}/parameters/manual` | Add a manual reading |
| `GET` | `/aquariums/{id}/parameters/manual/history` | History by `?from=...&to=...` (ISO-8601) |

### Target Parameters

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/aquariums/{id}/settings/targets` | Get target values (null if not configured) |
| `POST` | `/aquariums/{id}/settings/targets` | Save or update target values |

---

## Observability

Prometheus scrapes metrics from all services every 15 seconds via `/actuator/prometheus`.

- **Grafana:** http://localhost:3000 (admin / admin) — pre-built dashboard "Aquarium Microservices - Overview"
- **Prometheus:** http://localhost:9090 — raw metrics and target status

---

## Configuration

Database credentials and service URLs are passed as environment variables. Default values are safe for local development.

To change database credentials, update `DB_USER` and `DB_PASSWORD` in `docker-compose.yml` (they propagate automatically to all services).

To point services at external URLs, override the environment variables in `docker-compose.yml`:

```yaml
SPECIES_SERVICE_URL: http://my-host:8083/species
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 |
| Gateway | Spring Cloud Gateway |
| Persistence | Spring Data JPA, Hibernate |
| Migrations | Flyway |
| Database | PostgreSQL 16 |
| Resilience | Resilience4j (circuit breaker + retry) |
| Containerization | Docker, Docker Compose |
| Metrics | Prometheus, Grafana |
| Build | Maven |
| Utilities | Lombok, Springdoc OpenAPI |

---

## Previous Version

This project is the microservice evolution of a previous monolithic backend: [aquarium-monitor](https://github.com/F3rren/aquarium-monitor).

---

## License

MIT © F3rren
