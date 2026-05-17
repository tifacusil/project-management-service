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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/projects/{project_id}/equipment")
@Tag(name = "Equipment", description = "Gestión de equipamiento por proyecto")
public class EquipmentController {

	private final EquipmentService equipmentService;

	public EquipmentController(EquipmentService equipmentService) {
		this.equipmentService = equipmentService;
	}

	@GetMapping
	@Operation(summary = "Listar equipamiento", description = "Lista el equipamiento asociado a un proyecto.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK",
					content = @Content(schema = @Schema(implementation = Equipment.class))),
			@ApiResponse(responseCode = "404", description = "Proyecto no existe")
	})
	public List<Equipment> list(@Parameter(description = "ID del proyecto", example = "shangtan") @PathVariable("project_id") String projectId) {
		return equipmentService.listByProject(projectId);
	}

	@PostMapping
	@Operation(summary = "Registrar equipo", description = "Registra un equipo para un proyecto.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Creado",
					content = @Content(schema = @Schema(implementation = Equipment.class))),
			@ApiResponse(responseCode = "400", description = "Validación falla"),
			@ApiResponse(responseCode = "404", description = "Proyecto no existe")
	})
	public ResponseEntity<Equipment> create(
			@Parameter(description = "ID del proyecto", example = "shangtan") @PathVariable("project_id") String projectId,
			@RequestBody Equipment body) {
		Equipment created = equipmentService.create(projectId, body);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}
}
