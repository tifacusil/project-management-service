package com.projectmanagement.project_management.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.projectmanagement.project_management.model.Project;

@Mapper
public interface ProjectMapper {

	@Select("""
			SELECT project_id, name, location_lat, location_lng, status, created_at
			FROM projects
			ORDER BY created_at DESC
			""")
	List<Project> findAll();

	@Select("""
			SELECT project_id, name, location_lat, location_lng, status, created_at
			FROM projects
			WHERE project_id = #{projectId}
			""")
	Project findById(@Param("projectId") String projectId);

	@Select("SELECT COUNT(*) FROM projects")
	int countAll();

	@Select("SELECT COUNT(*) FROM projects WHERE project_id = #{projectId}")
	int countById(@Param("projectId") String projectId);

	@Insert("""
			INSERT INTO projects (project_id, name, location_lat, location_lng, status)
			VALUES (#{projectId}, #{name}, #{locationLat}, #{locationLng}, COALESCE(#{status}, 'active'))
			""")
	int insert(Project project);

	@Update("""
			UPDATE projects
			SET name = #{name},
				location_lat = #{locationLat},
				location_lng = #{locationLng},
				status = COALESCE(#{status}, status)
			WHERE project_id = #{projectId}
			""")
	int update(Project project);

	@Update("UPDATE projects SET status = 'inactive' WHERE project_id = #{projectId}")
	int softDelete(@Param("projectId") String projectId);
}
