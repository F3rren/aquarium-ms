# species-service

Read-only catalog of fish and coral species. Serves as the source of truth for species data used by `inhabitants-service` when enriching inhabitant responses.

| Property | Value |
|----------|-------|
| Port | `8083` |
| Database schema | `inhabitants` (shared with `inhabitants-service`) |
| Migrations | Flyway disabled (`ddl-auto=update`) |
| Inter-service consumers | `inhabitants-service` |

---

## Architecture

```
SpeciesController
    │
    └── ISpeciesService → SpeciesService
            │
            ├── IFishRepository  (JPA)
            └── ICoralRepository (JPA)
```

All responses are wrapped in `ApiResponseDTO<T>`.

---

## API Endpoints

Base path: `/species`

### Fish

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `GET` | `/species/fish` | 200 | List all fish species, sorted by name. Metadata includes `count` |
| `GET` | `/species/fish/{id}` | 200 | Get a specific fish by ID |

### Corals

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `GET` | `/species/corals` | 200 | List all coral species, sorted by name. Metadata includes `count` |
| `GET` | `/species/corals/{id}` | 200 | Get a specific coral by ID |

All endpoints return `404 Not Found` if the requested species does not exist.

---

## Key Classes

| Class | Role |
|-------|------|
| `SpeciesController` | Unified REST controller for both fish and corals |
| `SpeciesService` | Fetches from repositories, maps to DTOs via `toFishDTO()` / `toCoralDTO()` |
| `IFishRepository` | JPA repository; exposes `findAllSortedByName()` custom query |
| `ICoralRepository` | JPA repository; exposes `findAllSortedByName()` custom query |
| `FishResponseDTO` | id, commonName, scientificName, family, minTankSize, maxSize, difficulty, reefSafe, temperament, diet, imageUrl, description, waterType |
| `CoralResponseDTO` | id, commonName, scientificName, type, minTankSize, maxSize, difficulty, lightRequirement, flowRequirement, placement, aggressive, feeding, description |
| `GlobalExceptionHandler` | `ResourceNotFoundException` → 404 |

---

## Data Model

### Fish

| Field | Type | Description |
|-------|------|-------------|
| `commonName` | String | Common name (e.g. "Clownfish") |
| `scientificName` | String | Scientific name |
| `family` | String | Taxonomic family |
| `minTankSize` | int | Minimum tank volume in litres |
| `maxSize` | int | Maximum body size in cm |
| `difficulty` | String | Care difficulty level |
| `reefSafe` | boolean | Compatible with reef tanks |
| `temperament` | String | Behavioral temperament |
| `diet` | String | Dietary requirements |
| `waterType` | String | `Marine`, `Freshwater`, etc. |

### Coral

| Field | Type | Description |
|-------|------|-------------|
| `commonName` | String | Common name (e.g. "Hammer Coral") |
| `scientificName` | String | Scientific name |
| `type` | String | `LPS`, `SPS`, `Soft`, etc. |
| `minTankSize` | int | Minimum tank volume in litres |
| `maxSize` | int | Maximum size in cm |
| `difficulty` | String | Care difficulty level |
| `lightRequirement` | String | Required light intensity |
| `flowRequirement` | String | Required water flow |
| `placement` | String | Preferred position in tank |
| `aggressive` | boolean | Whether it stings neighbouring corals |
| `feeding` | String | Feeding requirements |

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | — | JDBC URL for PostgreSQL |
| `DB_USERNAME` | — | Database username |
| `DB_PASSWORD` | — | Database password |

---

## Notes

- This service shares the `inhabitants` schema with `inhabitants-service`. Because of this, Flyway is **disabled** and `ddl-auto=update` is used — schema changes must be coordinated with `inhabitants-service` migrations.
- The service is intentionally read-only: no POST/PUT/DELETE endpoints are exposed. Species data is populated directly in the database.
