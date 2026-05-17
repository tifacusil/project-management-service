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

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@GetMapping
	public List<Project> listAll() {
		return projectService.listAll();
	}

	@GetMapping("/{project_id}")
	public Project getById(@PathVariable("project_id") String projectId) {
		return projectService.getByIdWithSensors(projectId);
	}

	@PostMapping
	public ResponseEntity<Project> create(@Valid @RequestBody Project body) {
		Project created = projectService.create(body);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{project_id}")
	public Project update(@PathVariable("project_id") String projectId, @RequestBody Project body) {
		return projectService.update(projectId, body);
	}

	@DeleteMapping("/{project_id}")
	public ResponseEntity<Void> softDelete(@PathVariable("project_id") String projectId) {
		projectService.softDelete(projectId);
		return ResponseEntity.noContent().build();
	}
}
