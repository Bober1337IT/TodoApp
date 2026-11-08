# Todoapp

Spring Boot API (JWT + tasks) with a Docker stack: Caddy (TLS + auth rate limit), Postgres, Prometheus, Grafana.

The frontend is only an HTTP client — this document focuses on **backend and DevOps**.

## Architecture

```text
Client (browser / curl)
        │  https://localhost
        ▼
     Caddy :443
        │  TLS (tls internal for local use)
        ├─ /api/v1/auth/*  → IP rate limit → backend:8080
        ├─ /api/*          → backend:8080
        └─ everything else → frontend:80
                │
                ▼
         Spring Boot
                │
         Postgres (db)
```

Monitoring (separate path): Prometheus scrapes `backend:8080/actuator/prometheus` and `caddy:2019/metrics` → Grafana.

## Backend stack

| Piece | Role |
|-------|------|
| Spring Security + JWT | Stateless auth via `Authorization: Bearer …` |
| `JwtAuthFilter` | Parses token → `UserPrincipal` in `SecurityContext` |
| `TaskRateLimitFilter` | Limits task writes **by userId** (after JWT) |
| Bucket4j (in-memory) | Buckets in JVM memory (fine for a single instance) |
| Actuator + Micrometer | Health + Prometheus metrics (HTTP, JVM, custom counters) |
| Postgres | Users and tasks |

### API (overview)

- `POST /api/v1/auth/register`, `POST /api/v1/auth/login` — public
- `GET/POST/PUT/DELETE /api/v1/tasks`, `PATCH /api/v1/tasks/{id}/status` — JWT required

### Rate limiting — two layers

1. **Caddy** — `login` / `register`, key = IP (`remote_host`). Rejected requests **never** reach Spring.
2. **Spring `TaskRateLimitFilter`** — after login:
   - `POST` / `PUT` / `DELETE` on `/api/v1/tasks` → bucket `tasks-write:{userId}` (30/min)
   - `PATCH …/status` → bucket `tasks-status:{userId}` (60/min)
   - responds with `429` + metric `tasks.rate_limit.rejected`

Spring-side auth rate limiting is intentionally unused / removable — auth limiting belongs on the proxy.

## Run

```bash
cp .env.example .env   # fill in secrets
docker compose up -d --build
```

| URL | What |
|-----|------|
| https://localhost | UI + API via Caddy (local cert — browser warning is expected) |
| http://localhost:3000 | Grafana |
| http://localhost:9090 | Prometheus |

Backend and frontend are **not** published on the host — entry point is Caddy.

## Metrics and dashboard

- Spring: `http_server_requests_*`, `jvm_*`, `tasks_rate_limit_rejected_total`
- Caddy auth rejects: `caddy_http_request_errors_total` (rate_limit middleware; do not confuse with task `429`s proxied through Caddy)
- Dashboard: Grafana → **Todoapp overview** (HTTP, limits, JVM)

## Key files

| Path | Description |
|------|-------------|
| `backend/` | Spring Boot code |
| `caddy/Caddyfile` | TLS, routing, auth rate limit |
| `caddy/Dockerfile` | Caddy + `caddy-ratelimit` plugin |
| `docker-compose.yml` | All services |
| `monitoring/prometheus/prometheus.yml` | Scrapes backend + Caddy |
| `monitoring/grafana/dashboards/` | Dashboard provisioning |
| `.env` | Secrets |

## Production (short)

- In the Caddyfile: real domain instead of `localhost`, drop `tls internal` → Let’s Encrypt
- Open 80/443; do not expose 5432/9090/3000 publicly
- In-memory Bucket4j does not scale across multiple backend replicas (use Redis / edge limits instead)
