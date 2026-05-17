package com.projectmanagement.project_management.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Health", description = "Salud del servicio")
public class HealthController {

	@GetMapping("/health")
	@Operation(summary = "Health check", description = "Retorna el estado del servicio.")
	public Map<String, String> health() {
		return Map.of(
				"status", "healthy",
				"service", "project-management"
		);
	}
}
