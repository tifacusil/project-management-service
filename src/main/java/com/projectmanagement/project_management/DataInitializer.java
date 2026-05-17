package com.projectmanagement.project_management;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.projectmanagement.project_management.mapper.ProjectMapper;
import com.projectmanagement.project_management.mapper.SensorMapper;
import com.projectmanagement.project_management.model.Project;
import com.projectmanagement.project_management.model.Sensor;

@Component
public class DataInitializer implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	private final ProjectMapper projectMapper;
	private final SensorMapper sensorMapper;

	private final boolean seedEnabled;

	public DataInitializer(ProjectMapper projectMapper, SensorMapper sensorMapper, @Value("${app.seed.enabled:true}") boolean seedEnabled) {
		this.projectMapper = projectMapper;
		this.sensorMapper = sensorMapper;
		this.seedEnabled = seedEnabled;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!seedEnabled) {
			return;
		}

		try {
			seedProjectsIfEmpty();
			seedSensorsIfEmpty();
		} catch (Exception e) {
			log.warn("Seed inicial omitido (no se pudo acceder a la base de datos): {}", e.getMessage());
		}
	}

	private void seedProjectsIfEmpty() {
		if (projectMapper.countAll() > 0) {
			return;
		}

		Project shangtan = new Project();
		shangtan.setProjectId("shangtan");
		shangtan.setName("Shangtan Landslide");
		shangtan.setLocationLat(23.45);
		shangtan.setLocationLng(110.12);
		shangtan.setStatus("active");

		Project lingzhan = new Project();
		lingzhan.setProjectId("lingzhan");
		lingzhan.setName("Lingzhan Landslide");
		lingzhan.setLocationLat(24.12);
		lingzhan.setLocationLng(106.56);
		lingzhan.setStatus("active");

		projectMapper.insert(shangtan);
		projectMapper.insert(lingzhan);
	}

	private void seedSensorsIfEmpty() {
		if (sensorMapper.countAll() > 0) {
			return;
		}

		ensureProjectExistsForSeed("shangtan", "Shangtan Landslide", 23.45, 110.12);
		ensureProjectExistsForSeed("lingzhan", "Lingzhan Landslide", 24.12, 106.56);

		List<Sensor> sensors = List.of(
				sensor("GXZM-DB01", "shangtan", "gnss", "Huace H3"),
				sensor("GXZM-DB02", "shangtan", "gnss", "Huace H3"),
				sensor("GXZM-DB03", "shangtan", "gnss", "Huace H3"),
				sensor("GXZM-DB04", "shangtan", "gnss", "Huace H3"),
				sensor("GXZM-DB05", "shangtan", "gnss", "Huace H3"),
				sensor("GXZM-DB06", "shangtan", "gnss", "Huace H3"),
				sensor("GXZM-DB07", "shangtan", "gnss", "Huace H3"),
				sensor("GXZM-DB08", "shangtan", "gnss", "Huace H3"),
				sensor("GXZM-BASE", "shangtan", "gnss", "Huace H3"),
				sensor("GXZM-RG01", "shangtan", "rainfall", "JFZ-01 digital rain gauge"),
				sensor("GXZM-CAM01", "shangtan", "camera", "Hikvision IDS-2DF843715X-AFW/SP"),
				sensor("GXZM-CAM02", "shangtan", "camera", "Hikvision IDS-2DF843715X-AFW/SP"),
				sensor("GXZM-CAM03", "shangtan", "camera", "Hikvision IDS-2DF843715X-AFW/SP"),
				sensor("LZ-DB01", "lingzhan", "gnss", "Huace H3"),
				sensor("LZ-DB02", "lingzhan", "gnss", "Huace H3"),
				sensor("LZ-DB03", "lingzhan", "gnss", "Huace H3"),
				sensor("LZ-BASE", "lingzhan", "gnss", "Huace H3")
		);

		for (Sensor sensor : sensors) {
			sensorMapper.insert(sensor);
		}
	}

	private void ensureProjectExistsForSeed(String projectId, String name, double lat, double lng) {
		if (projectMapper.countById(projectId) > 0) {
			return;
		}

		Project project = new Project();
		project.setProjectId(projectId);
		project.setName(name);
		project.setLocationLat(lat);
		project.setLocationLng(lng);
		project.setStatus("active");

		projectMapper.insert(project);
	}

	private Sensor sensor(String sensorId, String projectId, String sensorType, String model) {
		Sensor sensor = new Sensor();
		sensor.setSensorId(sensorId);
		sensor.setProjectId(projectId);
		sensor.setSensorType(sensorType);
		sensor.setModel(model);
		sensor.setStatus("active");
		return sensor;
	}
}
