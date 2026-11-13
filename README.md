# Todoapp

Spring Boot API (JWT + tasks) with a Docker stack: Caddy (TLS + auth rate limit), Postgres, Prometheus, Grafana, plus a React web UI and an Android client.

Clients are HTTP-only — they talk to the same JWT task API. Sections below cover **backend / DevOps** and **Android**.

## Architecture

```text
Client (browser / Android / curl)
        │  https://localhost  (web via Caddy)
        │  http://10.0.2.2:8080 or LAN:8080  (Android → backend, see below)
        ▼
     Caddy :443          (web entry)
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

Web entry point is Caddy (80/443). Backend is also published on host **8080** so Android (emulator/LAN) can call the API without TLS. Frontend container is not meant for direct host use.

## Metrics and dashboard

- Spring: `http_server_requests_*`, `jvm_*`, `tasks_rate_limit_rejected_total`
- Caddy auth rejects: `caddy_http_request_errors_total` (rate_limit middleware; do not confuse with task `429`s proxied through Caddy)
- Dashboard: Grafana → **Todoapp overview** (HTTP, limits, JVM)

## Android

Kotlin + Jetpack Compose client under `android/`. Same API as the web app (auth, tasks CRUD, status toggle).

### Stack

| Piece | Role |
|-------|------|
| Compose + Navigation | UI and screen graph (splash → login/register → tasks → add/edit) |
| Hilt | DI (`@HiltAndroidApp`, `@AndroidEntryPoint`, `hiltViewModel`) |
| Retrofit + OkHttp + Gson | HTTP client; `BuildConfig.BASE_URL` |
| DataStore | JWT + email (`TokenStore`) |
| Auth interceptor | Adds `Authorization: Bearer …` |

### Features (parity notes)

- Splash session check (`GET /tasks` with stored token)
- Login / register, signed-in email on task list, sign out
- Task list: pull-to-refresh, checkbox status, two-tap delete (3s confirm), add/edit forms with date picker
- Status is changed via checkbox only 

### Run

1. Start backend (at least API reachable on the host), e.g. `docker compose up -d` (backend is published on `8080` when configured that way for device/emulator use).
2. Point the app at the API via `android/local.properties` → `api.base.url` (Gradle → `BuildConfig.BASE_URL`).

```bash
# Emulator → host machine backend (default if unset)
./scripts/dev-lan.sh --emulator

# Physical phone on same Wi‑Fi → LAN IP of your PC
./scripts/dev-lan.sh

# Optional: also bring compose up
./scripts/dev-lan.sh --up
```

| Target | Typical `api.base.url` |
|--------|-------------------------|
| Emulator | `http://10.0.2.2:8080/` |
| Phone (LAN) | `http://<your-lan-ip>:8080/` |

Open `android/` in Android Studio, sync Gradle, run the `app` configuration.

`android/local.properties` is local (sdk path + API URL)

### Key paths

| Path | Description |
|------|-------------|
| `android/app/` | Application module |
| `android/.../core/network/` | OkHttp interceptors, `SafeApiCall` |
| `android/.../feature/auth/` | Login/register + `TokenStore` |
| `android/.../feature/task/` | List, form, repository, API DTOs |
| `android/.../navigation/` | `TodoNavHost` + graphs |
| `scripts/dev-lan.sh` | Writes `api.base.url` for emulator or phone |

## Key files

| Path | Description |
|------|-------------|
| `backend/` | Spring Boot code |
| `frontend/` | React web UI |
| `android/` | Android Compose client |
| `caddy/Caddyfile` | TLS, routing, auth rate limit |
| `caddy/Dockerfile` | Caddy + `caddy-ratelimit` plugin |
| `docker-compose.yml` | All services |
| `monitoring/prometheus/prometheus.yml` | Scrapes backend + Caddy |
| `monitoring/grafana/dashboards/` | Dashboard provisioning |
| `scripts/dev-lan.sh` | Android API base URL for LAN / emulator |
| `.env` | Secrets |