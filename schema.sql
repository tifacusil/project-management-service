-- ============================================================
-- Base de datos: project_management_db
-- Servicio: project_management (Spring Boot + MyBatis)
-- Tablas: projects, sensors, equipment
-- ============================================================

CREATE DATABASE IF NOT EXISTS project_management_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE project_management_db;

-- Proyectos de monitoreo
CREATE TABLE IF NOT EXISTS projects (
    project_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location_lat DOUBLE,
    location_lng DOUBLE,
    status ENUM('active','inactive') NOT NULL DEFAULT 'active',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Sensores instalados por proyecto
CREATE TABLE IF NOT EXISTS sensors (
    sensor_id VARCHAR(64) PRIMARY KEY,
    project_id VARCHAR(64) NOT NULL,
    sensor_type VARCHAR(32) NOT NULL,
    model VARCHAR(128),
    install_lat DOUBLE,
    install_lng DOUBLE,
    install_depth DOUBLE,
    status ENUM('active','inactive') NOT NULL DEFAULT 'active',
    installed_at DATETIME,
    INDEX idx_sensors_project (project_id),
    INDEX idx_sensors_project_status (project_id, status)
);

-- Equipamiento por proyecto
CREATE TABLE IF NOT EXISTS equipment (
    equipment_id VARCHAR(64) PRIMARY KEY,
    project_id VARCHAR(64) NOT NULL,
    type VARCHAR(64),
    model VARCHAR(128),
    serial VARCHAR(128),
    status ENUM('active','inactive') NOT NULL DEFAULT 'active',
    INDEX idx_equipment_project (project_id)
);
