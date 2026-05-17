package com.projectmanagement.project_management.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Sensor {

	@NotBlank
	@Size(max = 64)
	private String sensorId;

	@Size(max = 50)
	private String projectId;

	@Pattern(regexp = "^(gnss|rainfall|strain|soil_pressure|deep_displacement|camera)$")
	private String sensorType;

	@Size(max = 100)
	private String model;

	private Double installLat;

	private Double installLng;

	private Double installDepth;

	@Size(max = 20)
	private String status;

	private LocalDateTime installedAt;

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Double getInstallLat() {
		return installLat;
	}

	public void setInstallLat(Double installLat) {
		this.installLat = installLat;
	}

	public Double getInstallLng() {
		return installLng;
	}

	public void setInstallLng(Double installLng) {
		this.installLng = installLng;
	}

	public Double getInstallDepth() {
		return installDepth;
	}

	public void setInstallDepth(Double installDepth) {
		this.installDepth = installDepth;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getInstalledAt() {
		return installedAt;
	}

	public void setInstalledAt(LocalDateTime installedAt) {
		this.installedAt = installedAt;
	}
}
