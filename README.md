# MUHIZI ALAIN — Student ID: **22870**

# Vertical Farming Web — API Testing Guide

Spring Boot REST API with PostgreSQL and a React UI. This document explains configuration (**application.properties**), **ports** on backend and frontend, every API endpoint, and a **step-by-step** order to test the system (Postman, curl, or the web UI).

---

## 1. Prerequisites

| Requirement | Notes |
|-------------|--------|
| Java 17 | Used by Spring Boot 3.4.5 |
| Maven | Or use `mvnw.cmd` in the project root |
| PostgreSQL | Database name: `VerticalFarmingWeb_DB` (default server port **5432**) |
| Node.js 18+ | For the React frontend |

All backend settings live in `src/main/resources/application.properties` (see **Section 3**). For an empty database with `ddl-auto=validate`, create tables once:

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--spring.jpa.hibernate.ddl-auto=update"
```

Then restore `ddl-auto=validate` in `application.properties` and start normally.

---

## 2. Ports (backend and frontend)

The project uses **two HTTP ports** in development. They must both be free before you start the apps.

| Port | Layer | Config file | Role |
|------|--------|-------------|------|
| **22870** | **Backend** (Spring Boot) | `application.properties` → `server.port=22870` | REST API base URL. All `/api/...` requests hit this port when using Postman or curl directly. Matches student ID **22870**. |
| **22871** | **Frontend** (Vite + React) | `frontend/vite.config.js` → `server.port: 22871` | Browser UI only. Open **http://localhost:22871** during development. |
| **5432** | **PostgreSQL** (not this app) | `application.properties` → JDBC URL | Database server; Spring Boot connects here—do not confuse with 22870/22871. |

**How the frontend talks to the backend**

- The React app calls paths like `/api/growers/all` (relative URL).
- Vite **proxies** any request starting with `/api` to **http://localhost:22870** (see `frontend/vite.config.js`).
- So in the browser you use port **22871**; the proxy forwards API traffic to **22870**.
- For **Postman** or **curl**, call the backend directly: `http://localhost:22870/api/...` (no proxy).

**Direct URLs**

| Purpose | URL |
|---------|-----|
| API (Postman / curl) | `http://localhost:22870` |
| Web UI (browser) | `http://localhost:22871` |
| Example API call | `GET http://localhost:22870/api/dashboard/summary` |

Both dev servers use **strict** port binding (`22871` has `strictPort: true` in Vite). If a port is already in use, startup fails—stop the old Java or Node process first.

---

## 3. `application.properties` explained

File path: **`src/main/resources/application.properties`**

This file configures the Spring Boot application at startup. Do **not** use leading spaces before property keys; each key must start at column 1 (e.g. `spring.datasource.url`, not ` spring.datasource.url`).

### Current settings (reference)

```properties
spring.application.name=vertical-farming-web
server.port=22870

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/VerticalFarmingWeb_DB
spring.datasource.username=postgres
spring.datasource.password=alaindb
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Property reference

| Property | Value (example) | Description |
|----------|-----------------|-------------|
| `spring.application.name` | `vertical-farming-web` | Logical name of the app (logs, Spring context). |
| `server.port` | `22870` | **Backend HTTP port.** Tomcat listens here for REST requests. |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/VerticalFarmingWeb_DB` | JDBC connection to PostgreSQL: host `localhost`, DB port `5432`, database `VerticalFarmingWeb_DB`. |
| `spring.datasource.username` | `postgres` | PostgreSQL user for authentication. Change to match your machine. |
| `spring.datasource.password` | `alaindb` | PostgreSQL password. Use your local password; avoid committing real secrets to public repos. |
| `spring.datasource.driver-class-name` | `org.postgresql.Driver` | JDBC driver for PostgreSQL (required; do not use SQL Server driver for this project). |
| `spring.jpa.hibernate.ddl-auto` | `validate` | On startup, Hibernate **checks** that tables match entities; it does **not** create tables. Use `update` once for an empty DB, then switch back to `validate`. |
| `spring.jpa.show-sql` | `true` | Prints SQL statements to the console (useful when testing). |
| `spring.jpa.properties.hibernate.format_sql` | `true` | Pretty-prints SQL in logs for easier reading. |

### Database checklist before run

1. PostgreSQL is running on `localhost:5432`.
2. Database `VerticalFarmingWeb_DB` exists (`CREATE DATABASE "VerticalFarmingWeb_DB";`).
3. Username/password in the file match your PostgreSQL login.
4. Tables exist if `ddl-auto=validate` (or run one-time `update` as in Section 1).

### Frontend port (not in `application.properties`)

The UI port **22871** is **not** set in `application.properties`. It is defined only in **`frontend/vite.config.js`** (`server.port: 22871`) and in **`frontend/package.json`** for `npm run preview`. Keep backend **22870** and frontend **22871** in sync with those files when documenting or demoing.

---

## 4. Run the application

### Backend (API)

```powershell
cd vertical-farming-web
mvn spring-boot:run
```

- Base URL: **http://localhost:22870**
- Health check: `GET http://localhost:22870/api/dashboard/summary` → **200**

### Frontend (optional UI tests)

```powershell
cd frontend
npm install
npm run dev
```

- UI: **http://localhost:22871** (proxies `/api` to port 22870)

---

## 5. Recommended test order (step by step)

Follow this sequence so foreign keys and business rules succeed. After each step, note **UUIDs** from responses (via `GET .../all` or response bodies) for later steps.

| Step | Action | Why |
|------|--------|-----|
| **1** | `GET /api/dashboard/summary` | Confirm server and DB connection |
| **2** | Create **crop families** (`POST /api/crop-families/save`) | Growers need family IDs |
| **3** | Create **grow towers** (`POST /api/grow-towers/save`) | Growers need a tower |
| **4** | `GET /api/grow-towers/free` | Pick a free tower for a grower |
| **5** | Create **growers** (`POST /api/growers/save`) | Needs tower + crop family IDs |
| **6** | Create **crop batches** (`POST /api/crop-batches/save`) | Needed for planting cycles |
| **7** | Create **planting cycle** (`POST /api/planting-cycles/save`) | Needs batch + grower; status starts as `PLANNED` |
| **8** | Advance cycle status (`PATCH .../status`) | `PLANNED` → `GROWING` → `HARVESTED` |
| **9** | Create **harvest record** (`POST /api/harvest-records/save`) | Only when cycle is `HARVESTED` |
| **10** | Run queries (search, page, stats, dashboard) | Read-only verification |
| **11** | Test updates and edge cases (404, 409, 400) | See sections below |
| **12** | Test deletes (single, then optional `delete-all`) | See delete order at end |

**Planting cycle status rules**

- `PLANNED` → `GROWING` or `FAILED`
- `GROWING` → `HARVESTED` or `FAILED`
- No transitions from `HARVESTED` or `FAILED`

**Planting date:** Must not be in the past when creating a cycle.

---

## 6. Sample request bodies (copy for Postman)

Replace `{uuid}` placeholders with IDs from your database.

### Crop family

```json
POST /api/crop-families/save
{ "name": "Leafy greens" }
```

### Grow tower

```json
POST /api/grow-towers/save
{ "towerNumber": 1, "name": "Tower A" }
```

### Grower

```json
POST /api/growers/save
{
  "firstName": "Jean",
  "lastName": "Mukiza",
  "phone": "+250788000001",
  "growTower": { "id": "{grow-tower-uuid}" },
  "cropFamilies": [ { "id": "{crop-family-uuid}" } ]
}
```

### Crop batch

```json
POST /api/crop-batches/save
{
  "cropName": "lettuce",
  "variety": "Butterhead",
  "batchCode": "LET-2026-01",
  "supplier": "Kigali Seeds"
}
```

### Planting cycle

Use a **future or today** `plantingDate` (ISO date).

```json
POST /api/planting-cycles/save
{
  "plantingDate": "2026-11-15",
  "trays": 12,
  "cropBatch": { "id": "{crop-batch-uuid}" },
  "grower": { "id": "{grower-uuid}" }
}
```

### Harvest record

Query param: `plantingCycleId={cycle-uuid}` (cycle must be `HARVESTED`).

```json
POST /api/harvest-records/save?plantingCycleId={cycle-uuid}
{
  "qualityGrade": "A",
  "notes": "First harvest",
  "kilograms": 25
}
```

---

## 7. API reference and test descriptions

Base URL: `http://localhost:22870`

Legend: **Pass** = expected status and behavior for a valid test.

---

### 7.1 Dashboard

| Method | Path | Description | Expected |
|--------|------|-------------|----------|
| GET | `/api/dashboard/summary` | Returns counts: crop batches, growers, free towers, cycles today, planned cycles, harvest records | **200**, JSON map of numbers |

**Test:** Call after creating data; counts should increase when you add entities.

---

### 7.2 Crop families — `/api/crop-families`

| Method | Path | Description | Expected |
|--------|------|-------------|----------|
| POST | `/save` | Create family `{ "name": "..." }` | **201**, success message |
| GET | `/all` | List all families | **200**, array |
| GET | `/unused` | Families not linked to any grower | **200**, array |
| GET | `/{id}` | Get one by UUID | **200** or **404** |
| PUT | `/update/{id}` | Update name | **200** or **404** |
| DELETE | `/delete/{id}` | Delete one; clears grower links | **200** or **404** |
| DELETE | `/delete-all` | Delete all families (detaches growers first) | **200** |

**Tests**

1. Save two families → `GET /all` shows both.
2. `GET /unused` includes families with no growers.
3. `GET /{id}` with random UUID → **404**.
4. Delete family assigned to grower → **200** (links removed in service).

---

### 7.3 Grow towers — `/api/grow-towers`

| Method | Path | Description | Expected |
|--------|------|-------------|----------|
| POST | `/save` | `{ "towerNumber": int, "name": "..." }` | **201** |
| GET | `/all` | All towers | **200** |
| GET | `/free` | Towers not assigned to a grower | **200** |
| GET | `/{id}` | One tower | **200** or **404** |
| PUT | `/update/{id}` | Update number/name | **200** or **404** |
| DELETE | `/delete/{id}` | Delete one | **200** or **404** |
| DELETE | `/delete-all` | Delete all; clears grower tower references first | **200** |

**Tests**

1. Create tower → appears in `/free`.
2. Assign tower to grower → tower no longer in `/free`.
3. `PATCH` grower change tower (see growers) → old tower may become free again.

---

### 7.4 Growers — `/api/growers`

| Method | Path | Description | Expected |
|--------|------|-------------|----------|
| POST | `/save` | Create with tower + crop families | **201**, **404** if bad IDs, **400** otherwise |
| GET | `/all` | All growers | **200** |
| GET | `/by-crop-family?name=...` | Growers for family name | **200** |
| GET | `/{id}` | One grower | **200** or **404** |
| PUT | `/update/{id}` | Update first/last name, phone | **200** or **404** |
| DELETE | `/delete/{id}` | Delete grower | **200** or **404** |
| DELETE | `/delete-all` | Deletes harvest records, planting cycles, then all growers | **200** |
| PUT | `/{growerId}/crop-families/{cropFamilyId}` | Add family to grower | **200**, **409** if duplicate, **404** if missing |
| PATCH | `/{growerId}/grow-tower?towerNumber=` | Move grower to tower by number | **200**, **409** if tower taken, **404** |

**Tests**

1. Save grower with invalid tower UUID → **404**.
2. Add same crop family twice → **409**.
3. Two growers same tower number → second change → **409**.
4. `GET /by-crop-family?name=Leafy greens` after setup → lists matching growers.

---

### 7.5 Crop batches — `/api/crop-batches`

| Method | Path | Description | Expected |
|--------|------|-------------|----------|
| POST | `/save` | Create batch (unique `batchCode`) | **201** |
| GET | `/all` | All batches | **200** |
| GET | `/by-crop-name?cropName=lettuce` | Case-insensitive search | **200** |
| GET | `/of-grower/{growerId}` | Batches used in grower's cycles | **200** or **404** grower |
| GET | `/{id}` | One batch | **200** or **404** |
| PUT | `/update/{id}` | Update fields | **200** or **404** |
| DELETE | `/delete/{id}` | Delete one | **200** or **404** |
| DELETE | `/delete-all` | Deletes harvest records, planting cycles, then all batches | **200** |

**Tests**

1. `GET /by-crop-name?cropName=Lettuce` finds `lettuce` batches.
2. After planting cycle for grower, `GET /of-grower/{id}` returns related batches.

---

### 7.6 Planting cycles — `/api/planting-cycles`

| Method | Path | Description | Expected |
|--------|------|-------------|----------|
| POST | `/save` | New cycle; status `PLANNED` | **201**; **400** past date; **409** duplicate grower+date; **404** bad batch/grower |
| GET | `/all` | All cycles | **200** |
| GET | `/by-status?status=PLANNED` | Filter by enum | **200** |
| GET | `/between?start=2026-01-01&end=2026-12-31` | Date range | **200** |
| GET | `/stats/by-status` | Count per status | **200**, array of pairs |
| GET | `/page?page=0&size=5&sort=plantingDate,desc` | Paginated list | **200** |
| GET | `/{id}` | One cycle | **200** or **404** |
| PUT | `/update/{id}` | Update date, trays, batch, grower | **200** or **404** |
| DELETE | `/delete/{id}` | Delete if no harvest records | **200**, **409** if harvest exists, **404** |
| DELETE | `/delete-all` | Deletes all harvest records then all cycles | **200** |
| PATCH | `/{id}/status?status=GROWING` | Status transition | **200**, **400** invalid transition, **404** |
| PATCH | `/cancel-day?growerId=&date=` | Mark cycles FAILED for grower on date | **200**, message with count |

**Status test sequence (same cycle id)**

1. `PATCH ...?status=GROWING` from `PLANNED` → **200**
2. `PATCH ...?status=HARVESTED` from `GROWING` → **200**
3. `PATCH ...?status=PLANNED` from `HARVESTED` → **400**

**Other tests**

- Two cycles same grower same date (non-FAILED) → second save → **409**
- `GET /page?page=0&size=5&sort=plantingDate,desc` returns `content`, `totalElements`, etc.

---

### 7.7 Harvest records — `/api/harvest-records`

| Method | Path | Description | Expected |
|--------|------|-------------|----------|
| POST | `/save?plantingCycleId={uuid}` | Create record; date set server-side | **201**; **400** if cycle not `HARVESTED`; **404** bad cycle |
| GET | `/crop-batch/{batchCode}` | Records for batch code | **200** |
| DELETE | `/delete/{id}` | Delete one | **200** or **404** |
| DELETE | `/delete-all` | Delete all records | **200** |

**Tests**

1. Save on `PLANNED` cycle → **400** with message about HARVESTED only.
2. After harvest, `GET /crop-batch/LET-2026-01` lists records.
3. Delete planting cycle that still has harvest → **409** on cycle delete until harvest removed.

---

## 8. Testing with curl (examples)

Replace host and IDs as needed.

```powershell
# Summary
curl -s http://localhost:22870/api/dashboard/summary

# Create crop family
curl -s -X POST http://localhost:22870/api/crop-families/save -H "Content-Type: application/json" -d "{\"name\":\"Herbs\"}"

# List all growers
curl -s http://localhost:22870/api/growers/all

# Update cycle status
curl -s -X PATCH "http://localhost:22870/api/planting-cycles/{cycleId}/status?status=GROWING"
```

---

## 9. Testing with the React UI (checklist)

| Page | What to verify |
|------|----------------|
| Dashboard | Summary numbers match API |
| Crop families | CRUD, unused banner, **Delete all** |
| Grow towers | CRUD, free towers list, **Delete all** |
| Growers | Create with tower + families, edit, add family, change tower, **Delete all** |
| Crop batches | CRUD, search by crop name, **Delete all** |
| Planting cycles | Create, paginate, status buttons, filters, cancel day, **Delete all** |
| Harvest records | Create only on HARVESTED cycle, load by batch code, **Delete all** |

---

## 10. Delete operations — safe order

Single deletes respect foreign keys. **`delete-all`** on some resources also removes dependent data:

| Endpoint | Side effects |
|----------|----------------|
| `/api/harvest-records/delete-all` | Harvest records only |
| `/api/planting-cycles/delete-all` | Harvest records + all cycles |
| `/api/growers/delete-all` | Harvest records + cycles + all growers |
| `/api/crop-batches/delete-all` | Harvest records + cycles + all batches |
| `/api/crop-families/delete-all` | All families (grower links cleared) |
| `/api/grow-towers/delete-all` | Clears grower tower refs + all towers |

For a **full reset**, prefer: harvest → planting cycles → growers → crop batches → crop families → grow towers (or use each **delete-all** understanding the table above).

---

## 11. Common failures

| Symptom | Cause | Fix |
|---------|--------|-----|
| SQL Server / wrong host on startup | Old datasource config | Use PostgreSQL in `application.properties`; `mvn clean spring-boot:run` |
| `Schema-validation: missing table` | Empty DB with `validate` | One-time `ddl-auto=update` or run SQL schema |
| Port 22870 in use | Previous Java process | Stop other Spring Boot instance |
| UI cannot reach API | Backend not running | Start backend on 22870 before `npm run dev` |
| Harvest save fails | Cycle not HARVESTED | Patch status through GROWING to HARVESTED |

---

## 12. Project structure (reference)

- `src/main/java/.../controller` — REST endpoints  
- `src/main/java/.../service` — business logic (including `deleteAll`)  
- `src/main/java/.../repository` — JPA repositories  
- `src/main/java/.../entity` — JPA entities  
- `frontend/` — React + Vite UI  

---

**Author:** MUHIZI ALAIN · **Student ID:** 22870 · **API port:** 22870 · **UI port:** 22871
