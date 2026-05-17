package com.projectmanagement.project_management.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.projectmanagement.project_management.model.Equipment;

@Mapper
public interface EquipmentMapper {

	@Select("""
			SELECT equipment_id, project_id, type, model, serial, status
			FROM equipment
			WHERE project_id = #{projectId}
			ORDER BY equipment_id
			""")
	List<Equipment> findByProjectId(@Param("projectId") String projectId);

	@Select("SELECT COUNT(*) FROM equipment")
	int countAll();

	@Insert("""
			INSERT INTO equipment (equipment_id, project_id, type, model, serial, status)
			VALUES (#{equipmentId}, #{projectId}, #{type}, #{model}, #{serial}, COALESCE(#{status}, 'active'))
			""")
	int insert(Equipment equipment);
}
