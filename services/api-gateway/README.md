# api-gateway

Entry point for the entire `aquarium-ms` system. Routes all inbound HTTP traffic to the appropriate downstream microservice, enforces CORS, and logs every request with its duration.

| Property | Value |
|----------|-------|
| Port | `8080` |
| Technology | Spring Cloud Gateway (reactive, Netty) |
| Database | None |

---

## Responsibilities

- **Path-based routing** — each URL prefix is mapped to a specific downstream service
- **CORS** — configured globally; allowed origins are controlled via the `CORS_ALLOWED_ORIGINS` environment variable
- **Request logging** — `LoggingGlobalFilter` records method, path, status code and duration for every request
- **Swagger aggregation** — exposes a single Swagger UI at `/swagger-ui.html` that loads API docs from all services

---

## Architecture

```
Client
  │
  ▼
LoggingGlobalFilter          ← logs request in, logs response out
  │
  ▼
Route matching (application.yml)
  │
  ├─ /aquariums/**            → aquariums-service:8081
  ├─ /aquariums/*/inhabitants → inhabitants-service:8082
  ├─ /species/**              → species-service:8083
  ├─ /aquariums/*/tasks/**    → maintenance-service:8084
  ├─ /products/**             → maintenance-service:8084
  ├─ /aquariums/*/parameters/manual/** → manual-parameters-service:8086
  ├─ /aquariums/*/parameters/** → parameters-service:8085
  └─ /aquariums/*/settings/targets/** → target-parameter-service:8087
```

> Route ordering matters: more specific paths (e.g. `/parameters/manual/**`) must be declared before catch-all paths (e.g. `/parameters/**`).

---

## Key Components

### `LoggingGlobalFilter`

Implements `GlobalFilter` + `Ordered` (priority: `HIGHEST_PRECEDENCE`). Logs two lines per request:

```
Incoming request: GET /aquariums/1/inhabitants from /127.0.0.1:54321
Completed request: GET /aquariums/1/inhabitants - Status: 200 OK - Duration: 43ms
```

---

## Configuration (`application.yml`)

### Routes

Each route entry has:
- `id` — logical name
- `uri` — downstream service URL (resolved from environment variable with a Docker default)
- `predicates` — path pattern to match
- `filters` — optional rewrite rules (used for OpenAPI doc routes)

### Environment Variables

| Variable | Default (Docker) | Description |
|----------|------------------|-------------|
| `SERVER_PORT` | `8080` | Gateway listen port |
| `AQUARIUMS_SERVICE_URL` | `http://aquariums-service:8081` | Aquariums service URL |
| `INHABITANTS_SERVICE_URL` | `http://inhabitants-service:8082` | Inhabitants service URL |
| `SPECIES_SERVICE_URL` | `http://species-service:8083` | Species service URL |
| `MAINTENANCE_SERVICE_URL` | `http://maintenance-service:8084` | Maintenance service URL |
| `PARAMETERS_SERVICE_URL` | `http://parameters-service:8085` | Water parameters service URL |
| `MANUAL_PARAMETERS_SERVICE_URL` | `http://manual-parameters-service:8086` | Manual parameters service URL |
| `TARGET_PARAMETERS_SERVICE_URL` | `http://target-parameter-service:8087` | Target parameters service URL |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000` | Allowed CORS origins |

### CORS

All HTTP methods (`GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`) are allowed. Headers are unrestricted. Credentials are permitted.

### Swagger UI

The unified Swagger UI at `/swagger-ui.html` fetches API docs from each service through dedicated OpenAPI routes (`/aquariums-service/v3/api-docs`, `/inhabitants-service/v3/api-docs`, etc.).

### Actuator / Metrics

Exposes `health`, `info`, `metrics`, and `prometheus` endpoints. Prometheus scrapes `/actuator/prometheus` every 15 seconds.
