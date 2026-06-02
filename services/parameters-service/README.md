# parameters-service

Records and retrieves automated water parameter measurements (temperature, pH, salinity, ORP) for each aquarium. Supports time-windowed history queries and a convenience "latest" endpoint.

| Property | Value |
|----------|-------|
| Port | `8085` |
| Database schema | `parameters` |
| Migrations | Flyway (`V1__init`, `V2__add_kafka_tables`; history table: `flyway_schema_history_water`) |
| Kafka role | **Producer + Consumer** — publishes `ParameterMeasured` on every new reading; consumes `aquarium.lifecycle` for cascade delete; implements CQRS read model |

---

## Architecture

```
WaterParameterController
    │
    └── IParameterService → ParameterService
            │
            ├── IParameterRepository (JPA)  ← write model (water_parameters, append-only)
            ├── IParameterReadModelRepository (JPA)  ← read model (parameter_latest)
            └── ParameterEventPublisher ──► topic: parameter.measurements

topic: aquarium.lifecycle
    └── AquariumEventListener (@KafkaListener)
            ├── IParameterRepository.deleteAllByAquariumId()
            └── IParameterReadModelRepository.deleteByAquariumId()

topic: parameter.measurements
    └── ParameterMeasuredListener (@KafkaListener, groupId: parameters-cqrs)
            └── IParameterReadModelRepository.save()  ← updates CQRS read model
```

All responses use `ApiResponseDTO<T>`. The service layer maps entities to `ParameterDTO` — entities are never exposed directly.

---

## API Endpoints

Base path: `/aquariums/{id}/parameters`

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `POST` | `/aquariums/{id}/parameters` | 201 | Record a new water parameter measurement |
| `GET` | `/aquariums/{id}/parameters` | 200 | List readings. Optional `?limit=N` (default 10), sorted newest first |
| `GET` | `/aquariums/{id}/parameters/latest` | 200 | Most recent reading for the aquarium |
| `GET` | `/aquariums/{id}/parameters/history` | 200 | Historical data by period or date range |

#### History query options

| Param | Example | Description |
|-------|---------|-------------|
| `period` | `?period=week` | Predefined window: `day`, `week`, `month` |
| `from` + `to` | `?from=2024-01-01T00:00:00&to=2024-01-31T23:59:59` | Custom ISO-8601 date range |

If neither is provided, defaults to the last week. If an invalid date string is passed, the `GlobalExceptionHandler` returns 400 with a descriptive message.

---

## Key Classes

| Class | Role |
|-------|------|
| `WaterParameterController` | REST layer; delegates history routing logic to the service |
| `ParameterService` | CRUD + history logic; calls `ParameterEventPublisher` on save; `getLatestParameter()` reads from `ParameterReadModel` with fallback to write store |
| `IParameterRepository` | JPA write store; includes `deleteAllByAquariumId` |
| `ParameterReadModel` | JPA entity for `parameters.parameter_latest` — one row per aquarium, always reflects the last measurement |
| `IParameterReadModelRepository` | JPA read store; `findByAquariumId`, `deleteByAquariumId` |
| `ParameterEventPublisher` | Publishes `ParameterMeasuredEvent` to `parameter.measurements` after every `saveParameter()` |
| `AquariumEventListener` | Consumes `aquarium.lifecycle`; deletes write store + read model rows on `AquariumDeleted` |
| `ParameterMeasuredListener` | Consumes `parameter.measurements` (group `parameters-cqrs`); updates `parameter_latest` — this is the CQRS read side |
| `ProcessedEventRepository` | Idempotency guard for both listeners |
| `ParameterDTO` | Response: id, aquariumId, temperature, ph, salinity, orp, measuredAt |
| `CreateParameterDTO` | Request body (Bean Validation) |
| `GlobalExceptionHandler` | `ResourceNotFoundException` → 404, `DateTimeParseException` → 400, `IllegalArgumentException` → 400 |

---

## Data Model (`ParameterDTO`)

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `aquariumId` | Long | Owning aquarium |
| `temperature` | Double | Water temperature (°C) |
| `ph` | Double | pH level |
| `salinity` | int | Salinity (ppt) |
| `orp` | int | Oxidation-reduction potential (mV) |
| `measuredAt` | LocalDateTime | Timestamp of measurement |

---

## Period Mapping

The `period` query parameter is resolved to a date range using a switch expression:

| Period | Window |
|--------|--------|
| `day` | Last 24 hours |
| `week` | Last 7 days |
| `month` | Last 30 days |
| _(any other value)_ | Last 7 days (default) |

---

## CQRS

`GET /parameters/latest` no longer queries `MAX(measured_at)` on the full `water_parameters` table. Instead:

1. `POST /parameters` saves the measurement → calls `ParameterEventPublisher` → Kafka event on `parameter.measurements`
2. `ParameterMeasuredListener` consumes the event → upserts `parameter_latest` (one row per aquarium)
3. `GET /parameters/latest` reads from `parameter_latest` — O(1) by primary key

If the read model row does not exist yet (e.g. service restarted before consuming), `getLatestParameter()` falls back to the write store transparently.

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/aquarium_ms` | JDBC URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `root` | Database password |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker address |

---

## Database

Schema: `parameters`. Flyway history table: `flyway_schema_history_water` (avoids conflicts with `manual-parameters-service` and `target-parameter-service` which share the same schema).  
`ddl-auto=validate`.

| Migration | Description |
|-----------|-------------|
| `V1__init` | `water_parameters` table |
| `V2__add_kafka_tables` | `parameter_latest` (CQRS read model) + `processed_events` (idempotency) |
