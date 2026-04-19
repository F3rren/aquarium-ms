# maintenance-service

Manages two independent domains for aquarium upkeep: **maintenance tasks** (recurring activities tied to a specific aquarium) and **products** (a shared inventory of treatments, additives, and equipment).

| Property | Value |
|----------|-------|
| Port | `8084` |
| Database schema | `maintenance` |
| Migrations | Flyway |

---

## Architecture

```
MaintenanceTaskController        ProductController
    │                                │
    └── IMaintenanceTaskService      └── IProductService
            │                               │
            └── IMaintenanceTaskRepository  └── IProductRepository (JPA)
                     (JPA)
```

Both controllers share the same `ApiResponseDTO<T>` wrapper.  
Product filtering is encapsulated in a `ProductFilter` value object — the controller builds it from query params and passes it to the service, keeping routing logic out of the controller.

---

## API Endpoints

### Maintenance Tasks

Base path: `/aquariums/{id}/tasks`

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `GET` | `/aquariums/{id}/tasks` | 200 | List tasks. Optional `?status=pending\|completed` |
| `POST` | `/aquariums/{id}/tasks` | 201 | Create a new task |
| `PUT` | `/aquariums/{id}/tasks/{taskId}` | 200 | Update a task |
| `DELETE` | `/aquariums/{id}/tasks/{taskId}` | 204 | Delete a task |
| `POST` | `/aquariums/{id}/tasks/{taskId}/complete` | 200 | Mark a task as completed (sets `completedAt`) |

### Products

Base path: `/products`

| Method | Path | Status | Description |
|--------|------|--------|-------------|
| `GET` | `/products` | 200 | List products with optional filters (see below) |
| `GET` | `/products/categories` | 200 | List distinct product categories |
| `GET` | `/products/{id}` | 200 | Get product by ID |
| `POST` | `/products` | 201 | Create a product |
| `PUT` | `/products/{id}` | 200 | Update a product |
| `PATCH` | `/products/{id}/mark-used` | 200 | Record current date as last use date |
| `PATCH` | `/products/{id}/toggle-favorite` | 200 | Toggle the favorite flag |
| `PATCH` | `/products/{id}/quantity` | 200 | Update product quantity |
| `DELETE` | `/products/{id}` | 204 | Delete a product |

#### Product filters (`GET /products`)

| Param | Type | Description |
|-------|------|-------------|
| `category` | String | Filter by category |
| `brand` | String | Filter by brand |
| `search` | String | Full-text search on name/description |
| `favorites` | boolean | Return only favorites |
| `expired` | boolean | Return expired products |
| `expiringSoon` | boolean | Return products expiring soon |
| `lowStock` | boolean | Return products below minimum stock |
| `shouldUseAgain` | boolean | Return products that should be re-used |

---

## Key Classes

| Class | Role |
|-------|------|
| `MaintenanceTaskController` | REST layer for tasks |
| `MaintenanceTaskService` | Business logic: CRUD, status filtering, ownership validation, task completion |
| `IMaintenanceTaskRepository` | JPA repository with queries by aquarium ID and completion status |
| `ProductController` | REST layer for products; builds `ProductFilter` from query params |
| `ProductService` | Business logic: CRUD, all filter-based queries, computed fields |
| `IProductRepository` | JPA repository |
| `ProductFilter` | Value object encapsulating all filter parameters |
| `MaintenanceTaskDTO` | Response: id, aquariumId, title, description, frequency, priority, isCompleted, dueDate, completedAt, createdAt, notes |
| `ProductDTO` | Response: all product fields + computed: `expired`, `expiringSoon`, `lowStock`, `daysSinceLastUse`, `shouldUseAgain` |
| `GlobalExceptionHandler` | `ResourceNotFoundException` → 404, `IllegalArgumentException` → 400 |

---

## Ownership Validation

Both task and product write operations validate that the resource belongs to the correct aquarium before applying changes. Violations throw `IllegalArgumentException` → 400 Bad Request.

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/aquarium_ms` | JDBC URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `root` | Database password |

---

## Database

Schema: `maintenance`. Migrations managed by Flyway.  
`ddl-auto=validate`.
