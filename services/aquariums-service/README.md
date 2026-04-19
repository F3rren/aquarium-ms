# aquariums-service

Manages the lifecycle of aquarium tanks. Also acts as a **proxy** for all parameter services, so the frontend only needs to talk to one endpoint per aquarium — parameter calls are forwarded transparently with circuit breaker protection.

| Property | Value |
|----------|-------|
| Port | `8081` |
| Database schema | `core` |
| Migrations | Flyway |
| Resilience | Resilience4j (circuit breaker + retry) |

---

## Architecture

```
AquariumController
    │
    ├── IAquariumService → AquariumService → IAquariumRepository (JPA)
    │
    └── ParametersClient ──► parameters-service      (circuit breaker: waterParameters)
                         ──► manual-parameters-service (circuit breaker: manualParameters)
                         ──► target-parameter-service  (circuit breaker: targetParameters)
```

All responses are wrapped in `ApiResponseDTO<T>` with fields `success`, `message`, `data`, `metadata`.

---

## API Endpoints

Base path: `/aquariums`

### Aquarium CRUD

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `GET` | `/aquariums` | 200 | Paginated list of aquariums (`?page`, `?size`, `?sort`; default: page 0, size 20, sorted by id) |
| `GET` | `/aquariums/{id}` | 200 | Get aquarium by ID |
| `POST` | `/aquariums` | 201 | Create a new aquarium |
| `PUT` | `/aquariums/{id}` | 200 | Update an existing aquarium |
| `DELETE` | `/aquariums/{id}` | 204 | Delete an aquarium |

### Water Parameters (proxy → `parameters-service`)

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/aquariums/{id}/water-parameters` | Record a new water parameter measurement |
| `GET` | `/aquariums/{id}/water-parameters` | List readings (`?limit=N`, max 100, default 10) |
| `GET` | `/aquariums/{id}/water-parameters/latest` | Most recent reading |
| `GET` | `/aquariums/{id}/water-parameters/history` | History by `?period=day\|week\|month` or `?from=...&to=...` (ISO-8601) |

### Manual Parameters (proxy → `manual-parameters-service`)

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/aquariums/{id}/manual-parameters` | Record a manual measurement |
| `GET` | `/aquariums/{id}/manual-parameters` | List all manual measurements |
| `GET` | `/aquariums/{id}/manual-parameters/latest` | Most recent manual measurement |
| `GET` | `/aquariums/{id}/manual-parameters/history` | History by `?from=...&to=...` (ISO-8601) |

### Target Parameters (proxy → `target-parameter-service`)

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/aquariums/{id}/target-parameters` | Get target values (null if not yet configured) |
| `POST` | `/aquariums/{id}/target-parameters` | Save or update target values |

---

## Key Classes

| Class | Role |
|-------|------|
| `AquariumController` | REST layer; validates input, delegates to service or `ParametersClient` |
| `AquariumService` | Business logic: CRUD operations, validation |
| `IAquariumRepository` | Spring Data JPA repository (`JpaRepository<Aquarium, Long>`) |
| `ParametersClient` | HTTP client for the three parameter services; applies circuit breaker and retry via Resilience4j |
| `AquariumResponseDTO` | Response shape (maps from `Aquarium` entity via `fromEntity()`) |
| `CreateAquariumDTO` | Request body for creation (Bean Validation) |
| `UpdateAquariumDTO` | Request body for update (Bean Validation) |
| `GlobalExceptionHandler` | Handles `ResourceNotFoundException` → 404, `IllegalArgumentException` → 400, validation errors → 400 |

---

## Resilience4j Configuration

Three circuit breaker instances (one per parameter service):

| Instance | Sliding window | Failure threshold | Open-state wait | Half-open calls |
|----------|---------------|-------------------|-----------------|-----------------|
| `waterParameters` | 10 | 50% | 30 s | 3 |
| `manualParameters` | 10 | 50% | 30 s | 3 |
| `targetParameters` | 10 | 50% | 30 s | 3 |

Each instance also has a **retry** policy: 3 attempts, 500 ms wait between retries.

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | — | JDBC URL for PostgreSQL |
| `DB_USERNAME` | — | Database username |
| `DB_PASSWORD` | — | Database password |
| `WATER_PARAMETERS_URL` | `http://localhost:8085/api` | parameters-service base URL |
| `MANUAL_PARAMETERS_URL` | `http://localhost:8086/api` | manual-parameters-service base URL |
| `TARGET_PARAMETERS_URL` | `http://localhost:8087/api` | target-parameter-service base URL |

---

## Database

Schema: `core`. Migrations in `src/main/resources/db/migration/` managed by Flyway.  
`ddl-auto=validate` — Hibernate validates the schema against migrations but never modifies it.
