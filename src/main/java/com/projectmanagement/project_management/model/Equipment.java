package com.projectmanagement.project_management.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Equipment {

	@NotBlank
	@Size(max = 64)
	private String equipmentId;

	@Size(max = 50)
	private String projectId;

	@Pattern(regexp = "^(DTU|solar_panel|battery|chassis|camera)$")
	private String type;

	@Size(max = 100)
	private String model;

	@Size(max = 100)
	private String serial;

	@Size(max = 20)
	private String status;

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
