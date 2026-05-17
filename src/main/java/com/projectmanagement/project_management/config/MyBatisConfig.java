package com.projectmanagement.project_management.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.projectmanagement.project_management.mapper")
public class MyBatisConfig {
}
