package com.projectmanagement.project_management.controller;

import java.util.List;

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

import com.projectmanagement.project_management.model.Sensor;
import com.projectmanagement.project_management.service.SensorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/projects/{project_id}/sensors")
@Tag(name = "Sensors", description = "Gestión de sensores por proyecto")
public class SensorController {

	private final SensorService sensorService;

	public SensorController(SensorService sensorService) {
		this.sensorService = sensorService;
	}

	@GetMapping
	@Operation(summary = "Listar sensores", description = "Lista los sensores de un proyecto.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK",
					content = @Content(schema = @Schema(implementation = Sensor.class))),
			@ApiResponse(responseCode = "404", description = "Proyecto no existe")
	})
	public List<Sensor> list(@Parameter(description = "ID del proyecto", example = "shangtan") @PathVariable("project_id") String projectId) {
		return sensorService.listByProject(projectId);
	}

	@GetMapping("/{sensor_id}")
	@Operation(summary = "Obtener sensor", description = "Obtiene el detalle de un sensor de un proyecto.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK",
					content = @Content(schema = @Schema(implementation = Sensor.class))),
			@ApiResponse(responseCode = "404", description = "Proyecto o sensor no existe")
	})
	public Sensor get(@Parameter(description = "ID del proyecto", example = "shangtan") @PathVariable("project_id") String projectId,
			@Parameter(description = "ID del sensor", example = "GXZM-DB01") @PathVariable("sensor_id") String sensorId) {
		return sensorService.getById(projectId, sensorId);
	}

	@PostMapping
	@Operation(summary = "Registrar sensor", description = "Registra un sensor para un proyecto.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Creado",
					content = @Content(schema = @Schema(implementation = Sensor.class))),
			@ApiResponse(responseCode = "400", description = "Validación falla"),
			@ApiResponse(responseCode = "404", description = "Proyecto no existe")
	})
	public ResponseEntity<Sensor> create(
			@Parameter(description = "ID del proyecto", example = "shangtan") @PathVariable("project_id") String projectId,
			@RequestBody Sensor body) {
		Sensor created = sensorService.create(projectId, body);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{sensor_id}")
	@Operation(summary = "Actualizar sensor", description = "Actualiza un sensor existente.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK",
					content = @Content(schema = @Schema(implementation = Sensor.class))),
			@ApiResponse(responseCode = "400", description = "Validación falla"),
			@ApiResponse(responseCode = "404", description = "Proyecto o sensor no existe")
	})
	public Sensor update(@Parameter(description = "ID del proyecto", example = "shangtan") @PathVariable("project_id") String projectId,
			@Parameter(description = "ID del sensor", example = "GXZM-DB01") @PathVariable("sensor_id") String sensorId,
			@RequestBody Sensor body) {
		return sensorService.update(projectId, sensorId, body);
	}

	@DeleteMapping("/{sensor_id}")
	@Operation(summary = "Eliminar sensor (soft delete)", description = "Marca el sensor como inactivo.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Sin contenido"),
			@ApiResponse(responseCode = "404", description = "Proyecto o sensor no existe")
	})
	public ResponseEntity<Void> softDelete(
			@Parameter(description = "ID del proyecto", example = "shangtan") @PathVariable("project_id") String projectId,
			@Parameter(description = "ID del sensor", example = "GXZM-DB01") @PathVariable("sensor_id") String sensorId) {
		sensorService.softDelete(projectId, sensorId);
		return ResponseEntity.noContent().build();
	}
}
