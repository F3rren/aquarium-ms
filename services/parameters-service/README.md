# parameters-service

Records and retrieves automated water parameter measurements (temperature, pH, salinity, ORP) for each aquarium. Supports time-windowed history queries and a convenience "latest" endpoint.

| Property | Value |
|----------|-------|
| Port | `8085` |
| Database schema | `parameters` |
| Migrations | Flyway (history table: `flyway_schema_history_water`) |

---

## Architecture

```
WaterParameterController
    │
    └── IParameterService → ParameterService
            │
            └── IParameterRepository (JPA)
```

All responses use `ApiResponseDTO<T>`. The service layer maps `Parameter` entities to `ParameterDTO` via a private `toDTO()` helper — entities are never exposed directly.

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
| `ParameterService` | CRUD + history logic; period → date range mapping via Java 17 switch expression |
| `IParameterRepository` | JPA: `findFirstByAquariumIdOrderByMeasuredAtDesc` (returns `Optional`), `findByAquariumIdOrderByMeasuredAtDesc`, `findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc` |
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

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/aquarium_ms` | JDBC URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `root` | Database password |

---

## Database

Schema: `parameters`. Flyway history table: `flyway_schema_history_water` (avoids conflicts with `manual-parameters-service` and `target-parameter-service` which share the same schema).  
`ddl-auto=validate`.
