package com.projectmanagement.project_management.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.projectmanagement.project_management.mapper.EquipmentMapper;
import com.projectmanagement.project_management.mapper.ProjectMapper;
import com.projectmanagement.project_management.model.Equipment;

@Service
public class EquipmentService {

	private static final Set<String> ALLOWED_TYPES = Set.of("DTU", "solar_panel", "battery", "chassis", "camera");

	private final ProjectMapper projectMapper;
	private final EquipmentMapper equipmentMapper;

	public EquipmentService(ProjectMapper projectMapper, EquipmentMapper equipmentMapper) {
		this.projectMapper = projectMapper;
		this.equipmentMapper = equipmentMapper;
	}

	public List<Equipment> listByProject(String projectId) {
		ensureProjectExists(projectId);
		return equipmentMapper.findByProjectId(projectId);
	}

	public Equipment create(String projectId, Equipment body) {
		ensureProjectExists(projectId);
		if (body == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body requerido");
		}
		if (body.getProjectId() != null && !body.getProjectId().equals(projectId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project_id del body no coincide con la URL");
		}
		if (body.getEquipmentId() == null || body.getEquipmentId().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "equipment_id es requerido");
		}
		if (body.getType() == null || body.getType().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type es requerido");
		}
		String normalizedType = body.getType().trim();
		if (!ALLOWED_TYPES.contains(normalizedType)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type inválido: " + body.getType());
		}

		Equipment toInsert = new Equipment();
		toInsert.setEquipmentId(body.getEquipmentId());
		toInsert.setProjectId(projectId);
		toInsert.setType(normalizedType);
		toInsert.setModel(body.getModel());
		toInsert.setSerial(body.getSerial());
		toInsert.setStatus(body.getStatus() != null ? body.getStatus() : "active");

		equipmentMapper.insert(toInsert);
		return toInsert;
	}

	private void ensureProjectExists(String projectId) {
		if (projectMapper.countById(projectId) == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe: " + projectId);
		}
	}
}
