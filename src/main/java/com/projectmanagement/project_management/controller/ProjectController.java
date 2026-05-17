package com.projectmanagement.project_management.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.project_management.model.Project;
import com.projectmanagement.project_management.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Gestión de proyectos de monitoreo")
public class ProjectController {

	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@GetMapping
	@Operation(summary = "Listar proyectos", description = "Lista todos los proyectos registrados.")
	public List<Project> listAll() {
		return projectService.listAll();
	}

	@GetMapping("/{project_id}")
	@Operation(summary = "Obtener proyecto", description = "Obtiene el detalle del proyecto e incluye los sensores asociados.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK",
					content = @Content(schema = @Schema(implementation = Project.class))),
			@ApiResponse(responseCode = "404", description = "Proyecto no existe")
	})
	public Project getById(@Parameter(description = "ID del proyecto", example = "shangtan") @PathVariable("project_id") String projectId) {
		return projectService.getByIdWithSensors(projectId);
	}

	@PostMapping
	@Operation(summary = "Crear proyecto", description = "Crea un proyecto nuevo.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Creado",
					content = @Content(schema = @Schema(implementation = Project.class))),
			@ApiResponse(responseCode = "400", description = "Validación falla")
	})
	public ResponseEntity<Project> create(@Valid @RequestBody Project body) {
		Project created = projectService.create(body);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{project_id}")
	@Operation(summary = "Actualizar proyecto", description = "Actualiza un proyecto existente.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK",
					content = @Content(schema = @Schema(implementation = Project.class))),
			@ApiResponse(responseCode = "400", description = "Validación falla"),
			@ApiResponse(responseCode = "404", description = "Proyecto no existe")
	})
	public Project update(@Parameter(description = "ID del proyecto", example = "shangtan") @PathVariable("project_id") String projectId,
			@RequestBody Project body) {
		return projectService.update(projectId, body);
	}

	@DeleteMapping("/{project_id}")
	@Operation(summary = "Eliminar proyecto (soft delete)", description = "Marca el proyecto como inactivo. No permite eliminar si existen sensores activos.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Sin contenido"),
			@ApiResponse(responseCode = "400", description = "Proyecto tiene sensores activos"),
			@ApiResponse(responseCode = "404", description = "Proyecto no existe")
	})
	public ResponseEntity<Void> softDelete(
			@Parameter(description = "ID del proyecto", example = "shangtan") @PathVariable("project_id") String projectId) {
		projectService.softDelete(projectId);
		return ResponseEntity.noContent().build();
	}
}
