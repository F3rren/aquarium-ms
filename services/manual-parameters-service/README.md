# manual-parameters-service

Records and retrieves manually entered chemical parameter measurements (calcium, magnesium, KH, nitrate, phosphate) for each aquarium. Designed for parameters that require dedicated test kits rather than automated probes.

| Property | Value |
|----------|-------|
| Port | `8086` |
| Database schema | `parameters` |
| Migrations | Flyway (history table: `flyway_schema_history_manual`) |

---

## Architecture

```
ManualParameterController
    │
    └── IManualParameterService → ManualParameterService
            │
            └── IManualParameterRepository (JPA)
```

All responses use `ApiResponseDTO<T>`. The service layer maps `ManualParameter` entities to `ManualParameterDTO` via `toDTO()` — entities are never exposed directly.

---

## API Endpoints

Base path: `/aquariums/{aquariumId}/parameters/manual`

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `POST` | `/aquariums/{aquariumId}/parameters/manual` | 201 | Record a new manual measurement |
| `GET` | `/aquariums/{aquariumId}/parameters/manual` | 200 | List measurements. Optional `?limit=N` (default 10), sorted newest first |
| `GET` | `/aquariums/{aquariumId}/parameters/manual/history` | 200 | History in a custom date range (`?from=...&to=...`, ISO-8601) |

> **Note on path priority:** the gateway routes `/aquariums/*/parameters/manual/**` before `/aquariums/*/parameters/**` to ensure manual parameter requests reach this service and not `parameters-service`.

#### Date format for history

Dates must follow ISO-8601 (`yyyy-MM-ddTHH:mm:ss`, e.g. `2024-01-15T10:30:00`). An invalid format returns 400 with the message: `Invalid date format. Expected ISO-8601 (e.g. 2024-01-15T10:30:00)`.

---

## Key Classes

| Class | Role |
|-------|------|
| `ManualParameterController` | REST layer |
| `ManualParameterService` | Business logic; `toDTO()` for entity → DTO mapping |
| `IManualParameterRepository` | JPA: `findFirstByAquariumIdOrderByMeasuredAtDesc` (returns `Optional`), `findByAquariumIdOrderByMeasuredAtDesc`, `findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc` |
| `ManualParameterDTO` | Response: id, aquariumId, calcium, magnesium, kh, nitrate, phosphate, measuredAt, notes |
| `CreateManualParameterDTO` | Request body (Bean Validation) |
| `GlobalExceptionHandler` | `ResourceNotFoundException` → 404, `DateTimeParseException` → 400 (with descriptive message), `IllegalArgumentException` → 400 |

---

## Data Model (`ManualParameterDTO`)

| Field | Type | Unit | Description |
|-------|------|------|-------------|
| `id` | Long | — | Primary key |
| `aquariumId` | Long | — | Owning aquarium |
| `calcium` | Double | mg/L | Calcium concentration |
| `magnesium` | Double | mg/L | Magnesium concentration |
| `kh` | Double | dKH | Carbonate hardness |
| `nitrate` | Double | mg/L | Nitrate concentration |
| `phosphate` | Double | mg/L | Phosphate concentration |
| `measuredAt` | LocalDateTime | — | Timestamp of measurement |
| `notes` | String | — | Optional free-text notes |

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/aquarium_ms` | JDBC URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `root` | Database password |

---

## Database

Schema: `parameters` (shared with `parameters-service` and `target-parameter-service`). Flyway history table: `flyway_schema_history_manual` — each service maintains its own migration history to avoid conflicts within the shared schema.  
`ddl-auto=validate`.
