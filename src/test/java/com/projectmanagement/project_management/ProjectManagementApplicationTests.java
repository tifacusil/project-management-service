package com.projectmanagement.project_management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"app.seed.enabled=false",
		"spring.datasource.url=jdbc:mysql://localhost:3306/project_management_db",
		"spring.datasource.username=test",
		"spring.datasource.password=test",
		"spring.datasource.hikari.initialization-fail-timeout=-1",
		"spring.datasource.hikari.minimum-idle=0"
})
class ProjectManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
