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

@RestController
@RequestMapping("/api/v1/projects/{project_id}/sensors")
public class SensorController {

	private final SensorService sensorService;

	public SensorController(SensorService sensorService) {
		this.sensorService = sensorService;
	}

	@GetMapping
	public List<Sensor> list(@PathVariable("project_id") String projectId) {
		return sensorService.listByProject(projectId);
	}

	@GetMapping("/{sensor_id}")
	public Sensor get(@PathVariable("project_id") String projectId, @PathVariable("sensor_id") String sensorId) {
		return sensorService.getById(projectId, sensorId);
	}

	@PostMapping
	public ResponseEntity<Sensor> create(@PathVariable("project_id") String projectId, @RequestBody Sensor body) {
		Sensor created = sensorService.create(projectId, body);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{sensor_id}")
	public Sensor update(@PathVariable("project_id") String projectId, @PathVariable("sensor_id") String sensorId,
			@RequestBody Sensor body) {
		return sensorService.update(projectId, sensorId, body);
	}

	@DeleteMapping("/{sensor_id}")
	public ResponseEntity<Void> softDelete(@PathVariable("project_id") String projectId, @PathVariable("sensor_id") String sensorId) {
		sensorService.softDelete(projectId, sensorId);
		return ResponseEntity.noContent().build();
	}
}
