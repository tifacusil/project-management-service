package com.projectmanagement.project_management.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.projectmanagement.project_management.mapper.ProjectMapper;
import com.projectmanagement.project_management.mapper.SensorMapper;
import com.projectmanagement.project_management.model.Sensor;

@Service
public class SensorService {

	private static final Set<String> ALLOWED_TYPES = Set.of(
			"gnss", "rainfall", "strain", "soil_pressure", "deep_displacement", "camera"
	);

	private final ProjectMapper projectMapper;
	private final SensorMapper sensorMapper;

	public SensorService(ProjectMapper projectMapper, SensorMapper sensorMapper) {
		this.projectMapper = projectMapper;
		this.sensorMapper = sensorMapper;
	}

	public List<Sensor> listByProject(String projectId) {
		ensureProjectExists(projectId);
		return sensorMapper.findByProjectId(projectId);
	}

	public Sensor getById(String projectId, String sensorId) {
		ensureProjectExists(projectId);
		Sensor sensor = sensorMapper.findByIdAndProjectId(projectId, sensorId);
		if (sensor == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor no existe: " + sensorId);
		}
		return sensor;
	}

	public Sensor create(String projectId, Sensor body) {
		ensureProjectExists(projectId);
		if (body == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body requerido");
		}
		if (body.getProjectId() != null && !body.getProjectId().equals(projectId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project_id del body no coincide con la URL");
		}
		if (body.getSensorId() == null || body.getSensorId().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sensor_id es requerido");
		}
		if (body.getSensorType() == null || body.getSensorType().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sensor_type es requerido");
		}
		String normalizedType = body.getSensorType().trim();
		if (!ALLOWED_TYPES.contains(normalizedType)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sensor_type inválido: " + body.getSensorType());
		}
		if (sensorMapper.countById(body.getSensorId()) > 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sensor_id ya existe: " + body.getSensorId());
		}

		Sensor toInsert = new Sensor();
		toInsert.setSensorId(body.getSensorId());
		toInsert.setProjectId(projectId);
		toInsert.setSensorType(normalizedType);
		toInsert.setModel(body.getModel());
		toInsert.setInstallLat(body.getInstallLat());
		toInsert.setInstallLng(body.getInstallLng());
		toInsert.setInstallDepth(body.getInstallDepth());
		toInsert.setStatus(body.getStatus());
		toInsert.setInstalledAt(body.getInstalledAt());

		sensorMapper.insert(toInsert);
		return getById(projectId, body.getSensorId());
	}

	public Sensor update(String projectId, String sensorId, Sensor body) {
		ensureProjectExists(projectId);
		if (body == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body requerido");
		}
		if (body.getProjectId() != null && !body.getProjectId().equals(projectId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project_id del body no coincide con la URL");
		}
		if (body.getSensorId() != null && !body.getSensorId().equals(sensorId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sensor_id del body no coincide con la URL");
		}

		Sensor existing = sensorMapper.findByIdAndProjectId(projectId, sensorId);
		if (existing == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor no existe: " + sensorId);
		}

		String newType = body.getSensorType() != null ? body.getSensorType().trim() : existing.getSensorType();
		if (newType != null && !ALLOWED_TYPES.contains(newType)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sensor_type inválido: " + body.getSensorType());
		}

		Sensor toUpdate = new Sensor();
		toUpdate.setProjectId(projectId);
		toUpdate.setSensorId(sensorId);
		toUpdate.setSensorType(newType);
		toUpdate.setModel(body.getModel() != null ? body.getModel() : existing.getModel());
		toUpdate.setInstallLat(body.getInstallLat() != null ? body.getInstallLat() : existing.getInstallLat());
		toUpdate.setInstallLng(body.getInstallLng() != null ? body.getInstallLng() : existing.getInstallLng());
		toUpdate.setInstallDepth(body.getInstallDepth() != null ? body.getInstallDepth() : existing.getInstallDepth());
		toUpdate.setStatus(body.getStatus() != null ? body.getStatus() : existing.getStatus());
		toUpdate.setInstalledAt(body.getInstalledAt() != null ? body.getInstalledAt() : existing.getInstalledAt());

		sensorMapper.update(toUpdate);
		return getById(projectId, sensorId);
	}

	public void softDelete(String projectId, String sensorId) {
		ensureProjectExists(projectId);
		Sensor existing = sensorMapper.findByIdAndProjectId(projectId, sensorId);
		if (existing == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor no existe: " + sensorId);
		}
		sensorMapper.softDelete(projectId, sensorId);
	}

	private void ensureProjectExists(String projectId) {
		if (projectMapper.countById(projectId) == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe: " + projectId);
		}
	}
}
