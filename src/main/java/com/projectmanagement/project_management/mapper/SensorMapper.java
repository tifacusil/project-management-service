package com.projectmanagement.project_management.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.projectmanagement.project_management.model.Sensor;

@Mapper
public interface SensorMapper {

	@Select("""
			SELECT sensor_id, project_id, sensor_type, model, install_lat, install_lng, install_depth, status, installed_at
			FROM sensors
			WHERE project_id = #{projectId}
			ORDER BY sensor_id
			""")
	List<Sensor> findByProjectId(@Param("projectId") String projectId);

	@Select("""
			SELECT sensor_id, project_id, sensor_type, model, install_lat, install_lng, install_depth, status, installed_at
			FROM sensors
			WHERE project_id = #{projectId} AND sensor_id = #{sensorId}
			""")
	Sensor findByIdAndProjectId(@Param("projectId") String projectId, @Param("sensorId") String sensorId);

	@Select("SELECT COUNT(*) FROM sensors")
	int countAll();

	@Select("SELECT COUNT(*) FROM sensors WHERE sensor_id = #{sensorId}")
	int countById(@Param("sensorId") String sensorId);

	@Select("SELECT COUNT(*) FROM sensors WHERE project_id = #{projectId} AND status = #{status}")
	int countByProjectIdAndStatus(@Param("projectId") String projectId, @Param("status") String status);

	@Insert("""
			INSERT INTO sensors (sensor_id, project_id, sensor_type, model, install_lat, install_lng, install_depth, status, installed_at)
			VALUES (#{sensorId}, #{projectId}, #{sensorType}, #{model}, #{installLat}, #{installLng}, #{installDepth}, COALESCE(#{status}, 'active'), #{installedAt})
			""")
	int insert(Sensor sensor);

	@Update("""
			UPDATE sensors
			SET sensor_type = COALESCE(#{sensorType}, sensor_type),
				model = #{model},
				install_lat = #{installLat},
				install_lng = #{installLng},
				install_depth = #{installDepth},
				status = COALESCE(#{status}, status),
				installed_at = #{installedAt}
			WHERE sensor_id = #{sensorId} AND project_id = #{projectId}
			""")
	int update(Sensor sensor);

	@Update("""
			UPDATE sensors
			SET status = 'inactive'
			WHERE sensor_id = #{sensorId} AND project_id = #{projectId}
			""")
	int softDelete(@Param("projectId") String projectId, @Param("sensorId") String sensorId);
}
