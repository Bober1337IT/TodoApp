# Todo frontend (v1)

React + TypeScript + Vite, Tailwind, TanStack Query, React Hook Form, Zod.

## Run

```bash
# terminal 1 — backend (JDK 27)
cd backend
export JAVA_HOME=/usr/lib/jvm/java-27
./mvnw spring-boot:run

# terminal 2 — frontend
cd frontend
npm install
npm run dev
```

Open http://localhost:5173 — Vite proxies `/api` to `http://localhost:8080`.

## v1 features

- List tasks
- Create task

Complete / delete come in v2 after backend endpoints exist.
