package com.projectmanagement.project_management.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.projectmanagement.project_management.mapper.ProjectMapper;
import com.projectmanagement.project_management.mapper.SensorMapper;
import com.projectmanagement.project_management.model.Project;
import com.projectmanagement.project_management.model.Sensor;

@Service
public class ProjectService {

	private final ProjectMapper projectMapper;
	private final SensorMapper sensorMapper;

	public ProjectService(ProjectMapper projectMapper, SensorMapper sensorMapper) {
		this.projectMapper = projectMapper;
		this.sensorMapper = sensorMapper;
	}

	public List<Project> listAll() {
		return projectMapper.findAll();
	}

	public Project getByIdWithSensors(String projectId) {
		Project project = projectMapper.findById(projectId);
		if (project == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe: " + projectId);
		}

		List<Sensor> sensors = sensorMapper.findByProjectId(projectId);
		project.setSensors(sensors);
		return project;
	}

	public Project create(Project project) {
		if (project == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body requerido");
		}
		if (project.getProjectId() == null || project.getProjectId().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project_id es requerido");
		}
		if (project.getName() == null || project.getName().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name es requerido");
		}
		if (projectMapper.countById(project.getProjectId()) > 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project_id ya existe: " + project.getProjectId());
		}

		projectMapper.insert(project);
		return getByIdWithSensors(project.getProjectId());
	}

	public Project update(String projectId, Project body) {
		if (body == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body requerido");
		}
		if (body.getProjectId() != null && !body.getProjectId().equals(projectId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project_id del body no coincide con la URL");
		}

		Project existing = projectMapper.findById(projectId);
		if (existing == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe: " + projectId);
		}

		Project toUpdate = new Project();
		toUpdate.setProjectId(projectId);
		toUpdate.setName(body.getName() != null ? body.getName() : existing.getName());
		toUpdate.setLocationLat(body.getLocationLat() != null ? body.getLocationLat() : existing.getLocationLat());
		toUpdate.setLocationLng(body.getLocationLng() != null ? body.getLocationLng() : existing.getLocationLng());
		toUpdate.setStatus(body.getStatus() != null ? body.getStatus() : existing.getStatus());

		projectMapper.update(toUpdate);
		return getByIdWithSensors(projectId);
	}

	public void softDelete(String projectId) {
		Project existing = projectMapper.findById(projectId);
		if (existing == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe: " + projectId);
		}

		int activeSensors = sensorMapper.countByProjectIdAndStatus(projectId, "active");
		if (activeSensors > 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se permite eliminar el proyecto con sensores activos");
		}

		projectMapper.softDelete(projectId);
	}
}
