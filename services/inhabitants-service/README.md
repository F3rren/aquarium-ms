# inhabitants-service

Manages the inhabitants (fish and corals) assigned to each aquarium. When returning inhabitant data, it **enriches** the response with species details fetched from `species-service` — if the species service is unavailable, it falls back gracefully and returns the inhabitant data without the species enrichment.

| Property | Value |
|----------|-------|
| Port | `8082` |
| Database schema | `inhabitants` |
| Migrations | Flyway |
| Inter-service dependency | `species-service` (via `SpeciesClient`) |

---

## Architecture

```
InhabitantController
    │
    └── IInhabitantService → InhabitantService
            │
            ├── IInhabitantRepository (JPA)
            │
            └── SpeciesClient ──► species-service:8083
                                  GET /species/fish/{id}
                                  GET /species/corals/{id}
```

All responses are wrapped in `ApiResponseDTO<T>`.

---

## API Endpoints

Base path: `/aquariums`

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `GET` | `/aquariums/{id}/inhabitants` | 200 | List inhabitants of an aquarium, enriched with species details. Metadata includes `totalCount`, `fishCount`, `coralCount` |
| `POST` | `/aquariums/{id}/inhabitants` | 201 | Add a new inhabitant to the aquarium |
| `PUT` | `/aquariums/{aquariumId}/inhabitants/{inhabitantId}` | 200 | Update an inhabitant (quantity, notes, custom name, current size) |
| `DELETE` | `/aquariums/{aquariumId}/inhabitants/{inhabitantId}` | 204 | Remove an inhabitant |

---

## Key Classes

| Class | Role |
|-------|------|
| `InhabitantController` | REST layer |
| `InhabitantService` | Business logic: CRUD, ownership checks, species enrichment via `SpeciesClient` |
| `IInhabitantRepository` | JPA repository with queries by aquarium ID |
| `SpeciesClient` | HTTP client for `species-service`; generic internal helper avoids code duplication between fish and coral fetches |
| `InhabitantDetailsDTO` | Response shape: base inhabitant fields + polymorphic `details` field (either `FishDTO` or `CoralDTO`) |
| `SpeciesDetailsDTO` | Marker interface implemented by `FishDTO` and `CoralDTO` for type-safe polymorphism |
| `InhabitantType` | Enum: `FISH`, `CORAL` — carries string values used for DB column and routing |
| `CreateInhabitantDTO` | Request body for creation |
| `UpdateInhabitantDTO` | Request body for update |
| `GlobalExceptionHandler` | `ResourceNotFoundException` → 404, `IllegalArgumentException` → 400 (ownership violations) |

---

## Inhabitant Type

Each inhabitant has an `inhabitantType` field stored as a string (`"fish"` or `"coral"`). On retrieval, the service calls the species service to fetch the corresponding details and populates the `details` field of the response accordingly:

```json
{
  "id": 1,
  "aquariumId": 1,
  "type": "fish",
  "speciesId": 3,
  "quantity": 2,
  "details": {
    "commonName": "Clownfish",
    "scientificName": "Amphiprioninae",
    "reefSafe": true,
    ...
  }
}
```

If `species-service` is unreachable, `details` is `null` and the rest of the response is still returned.

---

## Ownership Validation

All write operations (`PUT`, `DELETE`) verify that the inhabitant belongs to the specified aquarium. If not, an `IllegalArgumentException` is thrown → 400 Bad Request.

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/aquarium_ms` | JDBC URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `root` | Database password |
| `SPECIES_SERVICE_URL` | `http://species-service:8083/species` | Base URL for species lookups |

---

## Database

Schema: `inhabitants`. Migrations managed by Flyway.  
`ddl-auto=validate`.
