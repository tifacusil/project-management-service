package com.projectmanagement.project_management.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Project {

	@NotBlank
	@Size(max = 50)
	private String projectId;

	@NotBlank
	@Size(max = 200)
	private String name;

	private Double locationLat;

	private Double locationLng;

	@Size(max = 20)
	private String status;

	private LocalDateTime createdAt;

	private List<Sensor> sensors;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getLocationLat() {
		return locationLat;
	}

	public void setLocationLat(Double locationLat) {
		this.locationLat = locationLat;
	}

	public Double getLocationLng() {
		return locationLng;
	}

	public void setLocationLng(Double locationLng) {
		this.locationLng = locationLng;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<Sensor> getSensors() {
		return sensors;
	}

	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}
}
