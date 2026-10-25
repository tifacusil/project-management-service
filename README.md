# Project Management Service (Java)

Management microservice for the simulated landslide monitoring system. It manages **projects**, **sensors**, and **monitoring equipment**. This service is a logical prerequisite for other services (for example, inbound packet validation in Data Receiving requires projects/sensors to be registered).

---

## Table of contents

1. [Local development](#local-development)
2. [Configuration](#configuration)
3. [Database (MySQL)](#database-mysql)
4. [Initial data (seed)](#initial-data-seed)
5. [HTTP endpoints](#http-endpoints)
6. [OpenAPI / Swagger](#openapi--swagger)
7. [Deploy on Linux with systemd](#deploy-on-linux-with-systemd)
8. [Operations](#operations)
9. [Multiple instances](#multiple-instances)

---

## Local development

**Requirements:** Java 17.

```bash
# Build
./mvnw -q -DskipTests=false clean package

# Test
./mvnw -q test

# Run
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd -q -DskipTests=false clean package
mvnw.cmd -q test
mvnw.cmd spring-boot:run
```

Default HTTP port is `8084` (see `server.port` in `src/main/resources/application.yml`).

---

## Configuration

All configuration lives in `src/main/resources/application.yml` and can be overridden via environment variables (Spring Boot standard mapping rules apply).

This service supports `.env` (via `spring-dotenv`), so you can define variables in a `.env` file located in the process working directory. Start from the template: `cp .env.example .env`, then fill in `DB_PASSWORD` and `CONSUL_TOKEN`.

### Core

| Setting | Default | Env var equivalent |
|---|---:|---|
| `server.port` | `8084` | `SERVER_PORT` |

### MySQL

| Setting | Default | Env var equivalent |
|---|---:|---|
| `spring.datasource.url` | `${DB_URL:jdbc:mysql://204.168.203.32:3306/project_management_db}` | `DB_URL` or `SPRING_DATASOURCE_URL` |
| `spring.datasource.username` | `${DB_USERNAME:landslide}` | `DB_USERNAME` or `SPRING_DATASOURCE_USERNAME` |
| `spring.datasource.password` | `${DB_PASSWORD:}` | `DB_PASSWORD` or `SPRING_DATASOURCE_PASSWORD` |

### Seed

| Setting | Default | Env var equivalent |
|---|---:|---|
| `app.seed.enabled` | `true` | `APP_SEED_ENABLED` |

**Secret rule:** never commit secrets (for example, `DB_PASSWORD`) to git.

---

## Database (MySQL)

Database: `project_management_db`.

Required tables:

```sql
CREATE TABLE projects (
    project_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    location_lat DOUBLE,
    location_lng DOUBLE,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sensors (
    sensor_id VARCHAR(64) PRIMARY KEY,
    project_id VARCHAR(50) NOT NULL,
    sensor_type ENUM('gnss','rainfall','strain','soil_pressure','deep_displacement','camera') NOT NULL,
    model VARCHAR(100),
    install_lat DOUBLE,
    install_lng DOUBLE,
    install_depth DOUBLE,
    status VARCHAR(20) DEFAULT 'active',
    installed_at TIMESTAMP NULL,
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

CREATE TABLE equipment (
    equipment_id VARCHAR(64) PRIMARY KEY,
    project_id VARCHAR(50) NOT NULL,
    type ENUM('DTU','solar_panel','battery','chassis','camera') NOT NULL,
    model VARCHAR(100),
    serial VARCHAR(100),
    status VARCHAR(20) DEFAULT 'active',
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);
```

Data access: MyBatis with annotations (no XML).

---

## Initial data (seed)

On startup, if the tables are empty, the service inserts:

### Projects

- `shangtan` — "Shangtan Landslide" (lat `23.45`, lng `110.12`)
- `lingzhan` — "Lingzhan Landslide" (lat `24.12`, lng `106.56`)

### Sensors (paper IDs)

**Shangtan**
- GNSS Huace H3: `GXZM-DB01` to `GXZM-DB07`, `GXZM-DB08`, `GXZM-BASE`
- Rain gauge: `GXZM-RG01` (JFZ-01 digital rain gauge)
- Cameras: `GXZM-CAM01`, `GXZM-CAM02`, `GXZM-CAM03` (Hikvision IDS-2DF843715X-AFW/SP)

**Lingzhan**
- GNSS Huace H3: `LZ-DB01`, `LZ-DB02`, `LZ-DB03`, `LZ-BASE`

To disable seed:

```bash
export APP_SEED_ENABLED=false
```

---

## HTTP endpoints

### Health

| Method | Path | Description |
|---|---|---|
| GET | `/health` | Service health check |

### Projects

| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/projects` | List all projects |
| GET | `/api/v1/projects/{project_id}` | Project detail + associated sensors |
| POST | `/api/v1/projects` | Create project |
| PUT | `/api/v1/projects/{project_id}` | Update project |
| DELETE | `/api/v1/projects/{project_id}` | Soft delete (status=`inactive`) |

### Sensors (per project)

| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/projects/{project_id}/sensors` | List sensors |
| GET | `/api/v1/projects/{project_id}/sensors/{sensor_id}` | Sensor detail |
| POST | `/api/v1/projects/{project_id}/sensors` | Register sensor |
| PUT | `/api/v1/projects/{project_id}/sensors/{sensor_id}` | Update sensor |
| DELETE | `/api/v1/projects/{project_id}/sensors/{sensor_id}` | Soft delete |

### Equipment (per project)

| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/projects/{project_id}/equipment` | List equipment |
| POST | `/api/v1/projects/{project_id}/equipment` | Register equipment |

Main validations:

- `sensor.project_id` must reference an existing project.
- `sensor.sensor_type` must be one of: `gnss`, `rainfall`, `strain`, `soil_pressure`, `deep_displacement`, `camera`.
- `project_id` in the URL must match `project_id` in the body (when present).
- Deleting a project is not allowed if it has `active` sensors.

---

## OpenAPI / Swagger

- Swagger UI: `http://localhost:8084/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8084/v3/api-docs`

---

## Deploy on Linux with systemd

### Build artifact

```bash
./mvnw -q -DskipTests=false clean package
ls -lah target/*.jar
```

### Install on server

```bash
sudo useradd --system --no-create-home --shell /usr/sbin/nologin projectmgmt

sudo mkdir -p /opt/project-management-service
sudo cp target/*.jar /opt/project-management-service/app.jar
sudo chown -R projectmgmt:projectmgmt /opt/project-management-service
sudo chmod 750 /opt/project-management-service
```

### Environment variables (recommended)

```bash
sudo install -m 600 -o root -g root /dev/null /etc/project-management-service.env
sudo nano /etc/project-management-service.env
```

Example:

```bash
DB_URL=jdbc:mysql://204.168.203.32:3306/project_management_db
DB_USERNAME=landslide
DB_PASSWORD=...
APP_SEED_ENABLED=true
```

### Standard service (fixed port)

Create `/etc/systemd/system/project-management-service.service`:

```ini
[Unit]
Description=Project Management Service
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=projectmgmt
Group=projectmgmt
WorkingDirectory=/opt/project-management-service

EnvironmentFile=/etc/project-management-service.env
ExecStart=/usr/bin/java -jar /opt/project-management-service/app.jar

Restart=on-failure
RestartSec=5
SuccessExitStatus=143

NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=strict
ProtectHome=true
ReadWritePaths=/opt/project-management-service

[Install]
WantedBy=multi-user.target
```

Enable:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now project-management-service
sudo systemctl status project-management-service --no-pager
```

---

## Operations

Logs:

```bash
journalctl -u project-management-service -f
```

Quick check:

```bash
curl -s http://localhost:8084/health
curl -s http://localhost:8084/v3/api-docs | head
```

---

## Multiple instances

If you need to pass the port manually per instance, use a template unit `@` and `%i`.

File: `/etc/systemd/system/project-management-service@.service`

```ini
[Unit]
Description=Project Management Service (%i)
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=projectmgmt
Group=projectmgmt
WorkingDirectory=/opt/project-management-service

EnvironmentFile=/etc/project-management-service.env
ExecStart=/usr/bin/java -jar /opt/project-management-service/app.jar --server.port=%i

Restart=always
RestartSec=2
SuccessExitStatus=143

NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=strict
ProtectHome=true
ReadWritePaths=/opt/project-management-service

[Install]
WantedBy=multi-user.target
```

Start on a specific port:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now project-management-service@8084
sudo systemctl status project-management-service@8084 --no-pager
```
