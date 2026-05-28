# Recovery & Sports Therapy Management Platform

A premium SaaS platform for sports recovery, massage therapy, athlete monitoring,
injury tracking, rehabilitation, and recovery analytics.

The centerpiece is the **Interactive Body Map** — a fully SVG-driven,
region-addressable, severity-weighted, historically-queryable model of the human
body that powers heatmaps, trends, and athlete readiness scoring.

---

## Stack

| Layer        | Tech                                                                          |
| ------------ | ----------------------------------------------------------------------------- |
| Backend      | Java 21 · Spring Boot 3 · Spring Security · JPA/Hibernate · Flyway · MapStruct |
| Auth         | JWT access (15m) + Redis-backed refresh tokens (7d)                            |
| Database     | PostgreSQL 16                                                                  |
| Cache / WS   | Redis 7 · STOMP over WebSocket                                                 |
| Frontend     | Angular 18+ · Standalone APIs · Signals · Material · SCSS · PWA                |
| Infra        | Docker · Docker Compose · Nginx · GitHub Actions                               |

---

## Project layout

```
backend/      Spring Boot service (REST + WebSocket)
frontend/     Angular 18+ SPA / PWA
infra/        Nginx reverse-proxy config
.github/      CI/CD workflows
docs/         Architecture, deployment, body-map registry docs
```

---

## Local development (Docker)

```bash
cp .env.example .env
docker compose up --build
```

Then:

- Frontend (via nginx) → http://localhost
- Backend (direct)     → http://localhost:8080/actuator/health
- Swagger UI           → http://localhost/api/swagger-ui.html
- Postgres             → localhost:5432
- Redis                → localhost:6379

### Run services individually

```bash
# Backend only (requires Postgres + Redis running)
cd backend && ./mvnw spring-boot:run

# Frontend only (proxies /api to backend on :8080)
cd frontend && npm install && npm start
```

---

## Architectural highlights

- **Layered DDD-lite** on the backend: `controller → service → repository`,
  DTOs/mappers at the boundary, entities never leak past the service.
- **Stateless API**: JWT access tokens, refresh tokens stored in Redis with
  revocation support; rotating on each refresh.
- **Body Map contract**: SVG IDs (e.g. `R_QUAD_LEFT_ANT`) are the public
  contract between the SVG asset and the `body_region` table. Adding a region
  requires both a Flyway migration and an SVG asset update, enforced by tests.
- **Soft delete + audit** baked into the base entity — therapy data is
  immutable for compliance.
- **Angular Signals** for component and store state; **RxJS** reserved for
  streams (HTTP, WebSocket). No NgRx — Signals + a thin store pattern is enough
  and faster.
- **API versioning** via URL prefix `/api/v1`.

See [`docs/ARCHITECTURE.md`](./docs/ARCHITECTURE.md) for the deep dive.

---

## Quality gates

- Backend: JUnit 5 + Testcontainers (real Postgres + Redis), MockMvc slice tests.
- Frontend: Vitest/Jasmine + Angular Testing Library, Playwright for E2E.
- CI: GitHub Actions runs lint, unit, integration, and Docker build on every PR.

---

## License

Proprietary — all rights reserved.
