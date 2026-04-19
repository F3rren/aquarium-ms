# target-parameter-service

Stores the **desired target values** for water parameters (temperature, pH, salinity, ORP) for each aquarium. These targets can be used by the frontend or other services to compare against actual readings and trigger alerts.

Each aquarium can have at most one set of target parameters. Saving targets is an **upsert** operation — it creates the record on first call and updates it on subsequent calls.

| Property | Value |
|----------|-------|
| Port | `8087` |
| Database schema | `parameters` |
| Migrations | Flyway (history table: `flyway_schema_history_target`) |

---

## Architecture

```
TargetParameterController
    │
    └── ITargetParameterService → TargetParameterService
            │
            └── ITargetParameterRepository (JPA)
```

All responses use `ApiResponseDTO<T>`. The service maps `TargetParameter` entities to `TargetParameterResponseDTO` via a private `toDTO()` helper — entities are never exposed directly.

---

## API Endpoints

Base path: `/aquariums/{aquariumId}/settings/targets`

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `GET` | `/aquariums/{aquariumId}/settings/targets` | 200 | Get target values for the aquarium. Returns `data: null` if no targets have been configured yet |
| `POST` | `/aquariums/{aquariumId}/settings/targets` | 200 | Save or update target values (upsert) |

> The GET endpoint always returns 200, even when no targets are configured. The `data` field will be `null` and the message will be `"No custom target parameter found"`.

---

## Key Classes

| Class | Role |
|-------|------|
| `TargetParameterController` | REST layer; handles null data case explicitly |
| `TargetParameterService` | Business logic: upsert via `orElseGet`, `toDTO()` mapping |
| `ITargetParameterRepository` | JPA: `findByAquariumId` returns `Optional<TargetParameter>` |
| `TargetParameterResponseDTO` | Response: id, aquariumId, temperature, ph, salinity, orp |
| `SaveTargetParameterDTO` | Request body |
| `GlobalExceptionHandler` | `ResourceNotFoundException` → 404, `IllegalArgumentException` → 400, validation errors → 400 |

---

## Data Model (`TargetParameterResponseDTO`)

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `aquariumId` | Long | Owning aquarium (unique constraint — one record per aquarium) |
| `temperature` | Double | Target temperature (°C) |
| `ph` | Double | Target pH |
| `salinity` | Double | Target salinity (ppt) |
| `orp` | Double | Target ORP (mV) |

---

## Upsert Logic

```
POST /aquariums/{id}/settings/targets
  → findByAquariumId(id)
      ├─ present → update existing fields → save → return DTO
      └─ empty   → create new record with aquariumId → save → return DTO
```

All four fields are always overwritten on update. Partial updates are not supported — to preserve a value, the caller must re-send it.

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/aquarium_ms` | JDBC URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `root` | Database password |

---

## Database

Schema: `parameters` (shared with `parameters-service` and `manual-parameters-service`). Flyway history table: `flyway_schema_history_target` — independent migration history per service within the shared schema.  
`ddl-auto=validate`.
