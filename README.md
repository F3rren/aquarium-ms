# Aquarium Microservices

![Java 17](https://img.shields.io/badge/java-17-blue.svg)
![Spring Boot 3.3.5](https://img.shields.io/badge/spring--boot-3.3.5-green.svg)
![Microservices](https://img.shields.io/badge/architecture-microservices-blue.svg)
![Docker](https://img.shields.io/badge/docker-ready-blue.svg)
![PostgreSQL](https://img.shields.io/badge/database-PostgreSQL%2016-336791.svg)
![AWS ECS](https://img.shields.io/badge/deploy-AWS%20ECS%20Fargate-FF9900.svg)
![MIT License](https://img.shields.io/badge/license-MIT-green.svg)

A **microservice-based backend** for comprehensive aquarium management — tanks, inhabitants, species, maintenance tasks, and water parameters.

Built with **Java 17, Spring Boot 3.3.5, Spring Cloud Gateway** and **PostgreSQL 16**. Fully containerized via Docker Compose for local development and deployed to **AWS ECS Fargate** via GitHub Actions.

> **Note:** This backend is designed to work alongside the [Aquarium Interface](https://github.com/F3rren/Aquarium-interface) frontend and is not intended as a public/general-purpose API.

---

## Quick Start (local)

**Only prerequisite: [Docker Desktop](https://www.docker.com/products/docker-desktop/)**

```bash
git clone https://github.com/F3rren/aquarium-ms.git
cd aquarium-ms
docker-compose up -d
```

Docker Compose builds all images, starts PostgreSQL, all microservices, and the monitoring stack automatically. Wait ~30–60 seconds, then:

| Service | URL |
|---------|-----|
| API Gateway (main entrypoint) | http://localhost:8080 |
| Swagger UI (all services) | http://localhost:8080/swagger-ui.html |
| Grafana (metrics) | http://localhost:3000 |
| Prometheus | http://localhost:9090 |

```bash
docker-compose down        # stop
docker-compose down -v     # stop and delete volumes
```

---

## Architecture

All HTTP traffic goes through the **API Gateway** on port 8080, which routes requests to the appropriate microservice.

```
Client → API Gateway (8080) → aquariums-service           (8081)
                             → inhabitants-service         (8082)
                             → species-service             (8083)
                             → maintenance-service         (8084)
                             → parameters-service          (8085)
                             → manual-parameters-service   (8086)
                             → target-parameters-service   (8087)
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

Full interactive documentation: **http://localhost:8080/swagger-ui.html**

---

## Observability

Prometheus scrapes metrics from all services every 15 seconds via `/actuator/prometheus`. Grafana comes pre-configured with Prometheus as datasource.

- **Grafana:** http://localhost:3000 — pre-built dashboard "Aquarium Microservices - Overview"
- **Prometheus:** http://localhost:9090 — raw metrics and target status

---

## Deployment — AWS ECS Fargate

Each microservice has a dedicated GitHub Actions workflow under [`.github/workflows/`](.github/workflows/). Workflows trigger automatically on push to `main` when files inside the relevant service directory change.

### Pipeline

```
push to main
  └─ Test (PostgreSQL service container)
       └─ [on success] Build Docker image
            └─ Push to Amazon ECR  (:sha + :latest)
                 └─ Render new ECS task definition
                      └─ Deploy to ECS Fargate (waits for health check)
```

### Prerequisites

The AWS infrastructure must exist before the workflows can run:

- An **ECS cluster** and one **ECS service** per microservice
- An **ECR repository** per microservice
- An **IAM user** with permissions: `ecr:*`, `ecs:RegisterTaskDefinition`, `ecs:UpdateService`, `ecs:DescribeTaskDefinition`

### GitHub Secrets

Configure these in **Settings → Secrets and variables → Actions**:

| Secret | Description |
|--------|-------------|
| `AWS_ACCESS_KEY_ID` | IAM access key |
| `AWS_SECRET_ACCESS_KEY` | IAM secret key |
| `AWS_REGION` | e.g. `eu-west-1` |
| `ECR_REPOSITORY` | ECR repo name for `aquariums-service` |
| `ECS_CLUSTER` | ECS cluster name for `aquariums-service` |
| `ECS_SERVICE` | ECS service name for `aquariums-service` |
| `ECS_CONTAINER_NAME` | Container name in the task definition |
| `GATEWAY_ECR_REPOSITORY` | ECR repo name for `api-gateway` |
| `GATEWAY_ECS_CLUSTER` | ECS cluster name for `api-gateway` |
| `GATEWAY_ECS_SERVICE` | ECS service name for `api-gateway` |
| `GATEWAY_ECS_CONTAINER` | Container name for `api-gateway` |
| `INHABITANTS_ECR_REPOSITORY` | *(same pattern for `inhabitants-service`)* |
| `SPECIES_ECR_REPOSITORY` | *(same pattern for `species-service`)* |
| `MAINTENANCE_ECR_REPOSITORY` | *(same pattern for `maintenance-service`)* |
| `PARAMETERS_ECR_REPOSITORY` | *(same pattern for `parameters-service`)* |
| `MANUAL_PARAMETERS_ECR_REPOSITORY` | *(same pattern for `manual-parameters-service`)* |
| `TARGET_PARAMETER_ECR_REPOSITORY` | *(same pattern for `target-parameter-service`)* |

Each `INHABITANTS_*`, `SPECIES_*`, `MAINTENANCE_*`, `PARAMETERS_*`, `MANUAL_PARAMETERS_*` and `TARGET_PARAMETER_*` group follows the same four-secret pattern (`_ECR_REPOSITORY`, `_ECS_CLUSTER`, `_ECS_SERVICE`, `_ECS_CONTAINER`).

---

## Configuration

Copy `.env.example` to `.env` to override local defaults — the file is gitignored and never committed.

```bash
cp .env.example .env
```

| Variable | Default | Used by |
|----------|---------|---------|
| `DB_USER` | `postgres` | postgres container + all Spring services |
| `DB_PASSWORD` | `root` | same |
| `GF_ADMIN_USER` | `admin` | Grafana |
| `GF_ADMIN_PASSWORD` | `admin` | Grafana |

For running a service directly with `mvn spring-boot:run` (without Docker Compose), set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` as environment variables.

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
| Container Registry | Amazon ECR |
| Orchestration | Amazon ECS Fargate |
| CI/CD | GitHub Actions |
| Metrics | Prometheus, Grafana |
| Build | Maven |
| Utilities | Lombok |

---

## Previous Version

This project is the microservice evolution of a previous monolithic backend: [aquarium-monitor](https://github.com/F3rren/aquarium-monitor).

---

## License

MIT © F3rren
