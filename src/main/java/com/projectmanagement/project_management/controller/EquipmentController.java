package com.projectmanagement.project_management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.project_management.model.Equipment;
import com.projectmanagement.project_management.service.EquipmentService;

@RestController
@RequestMapping("/api/v1/projects/{project_id}/equipment")
public class EquipmentController {

	private final EquipmentService equipmentService;

	public EquipmentController(EquipmentService equipmentService) {
		this.equipmentService = equipmentService;
	}

	@GetMapping
	public List<Equipment> list(@PathVariable("project_id") String projectId) {
		return equipmentService.listByProject(projectId);
	}

	@PostMapping
	public ResponseEntity<Equipment> create(@PathVariable("project_id") String projectId, @RequestBody Equipment body) {
		Equipment created = equipmentService.create(projectId, body);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}
}
