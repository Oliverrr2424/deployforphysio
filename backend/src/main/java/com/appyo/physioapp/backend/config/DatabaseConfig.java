package com.appyo.physioapp.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.appyo.physioapp")
public class DatabaseConfig {
    // Using Spring Boot's auto-configuration for data source and JPA
} 