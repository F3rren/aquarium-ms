# Aquarium Microservices

![Java 17](https://img.shields.io/badge/java-17-blue.svg)
![Spring Boot 3.3.5](https://img.shields.io/badge/spring--boot-3.3.5-green.svg)
![Kafka](https://img.shields.io/badge/messaging-Apache%20Kafka-231F20.svg)
![Resilience4j](https://img.shields.io/badge/resilience-Resilience4j-blue.svg)
![Docker](https://img.shields.io/badge/docker-ready-blue.svg)
![PostgreSQL](https://img.shields.io/badge/database-PostgreSQL%2016-336791.svg)
![MIT License](https://img.shields.io/badge/license-MIT-green.svg)

Microservice backend for comprehensive aquarium management — tanks, inhabitants, species catalog, maintenance tracking, and water parameter monitoring.

Built with **Java 17, Spring Boot 3.3.5, Spring Cloud Gateway, Apache Kafka (KRaft), PostgreSQL 16, Resilience4j**. Fully containerized with Docker Compose, monitored via Prometheus and Grafana.

> Designed to work alongside the [Aquarium Interface](https://github.com/F3rren/Aquarium-interface) Flutter frontend.

---

## Quick Start (local)

**Only prerequisite: [Docker Desktop](https://www.docker.com/products/docker-desktop/)**

```bash
git clone https://github.com/F3rren/aquarium-ms.git
cd aquarium-ms
docker-compose up -d
```

| Service | URL |
|---------|-----|
| Homepage (dashboard) | http://localhost:3001 |
| API Gateway + Swagger UI | http://localhost:8080/swagger-ui.html |
| Kafka UI | http://localhost:8090 |
| Grafana | http://localhost:3000 (admin / admin) |
| Prometheus | http://localhost:9090 |

> **Homepage** is a static landing page (`ghcr.io/gethomepage/homepage`) aggregating every
> service, its live health, and the observability tools. Config lives in
> `observability/homepage/*.yaml`. It deliberately does **not** mount the Docker socket —
> health is checked over HTTP against each service's `/actuator/health` instead.

---

## Architecture

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

                                           manual-parameters-service :8086
                                           target-parameter-service  :8087
```

**Synchronous (HTTP):** `inhabitants-service` → `species-service` for species enrichment. `aquariums-service` → parameter services via circuit breaker + retry.

**Asynchronous (Kafka):**
- `aquarium.lifecycle` — cascade delete across inhabitants, parameters, maintenance
- `parameter.measurements` — triggers CQRS read model update and anomaly alert creation

---

## Key Patterns

| Pattern | Where | Purpose |
|---------|-------|---------|
| **Transactional Outbox** | `aquariums-service` | Zero message loss — events written to DB and Kafka atomically |
| **CQRS** | `parameters-service` | Separate write (`water_parameters`) and read (`parameter_latest`) models |
| **Idempotent consumer** | all consumers | Deduplication via `processed_events` table, safe with Kafka at-least-once |
| **`@RetryableTopic` + DLT** | all `@KafkaListener` | 3 retries with exponential backoff, failed events land on `.DLT` topic |
| **Circuit breaker + retry** | `aquariums-service` | Resilience4j protects inter-service HTTP calls |
| **Parameter anomaly alerts** | `maintenance-service` | Auto-creates high-priority task when temp or pH is out of safe range |

---

## Observability

![Aquarium Microservices Dashboard](docs/grafana-dashboard.png)

- **Grafana:** http://localhost:3000 — pre-built dashboard "Aquarium Microservices - Overview"
- **Prometheus:** http://localhost:9090 — raw metrics and target status

Request rate, JVM memory, active threads, and success rate across all 7 services. Dashboard auto-provisioned on `docker-compose up`.

---

## Configuration

Copy `.env.example` to `.env` to override local defaults — the file is gitignored and never committed.

```bash
cp .env.example .env
```

| Variable | Default | Used by |
|----------|---------|---------|
| `SPRING_PROFILES_ACTIVE` | _(empty)_ | all Spring services |
| `DB_USER` | `postgres` | postgres container + all Spring services |
| `DB_PASSWORD` | `root` | same |
| `GF_ADMIN_USER` | `admin` | Grafana |
| `GF_ADMIN_PASSWORD` | `admin` | Grafana |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 |
| Gateway | Spring Cloud Gateway |
| Persistence | Spring Data JPA, Hibernate, Flyway |
| Database | PostgreSQL 16 |
| Messaging | Apache Kafka 3.7 (KRaft), Spring Kafka |
| Resilience | Resilience4j (circuit breaker + retry) |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |
| Metrics | Prometheus, Grafana |

---

## Previous Version

Microservice evolution of [aquarium-monitor](https://github.com/F3rren/aquarium-monitor), the original monolithic backend.

---

MIT © F3rren
