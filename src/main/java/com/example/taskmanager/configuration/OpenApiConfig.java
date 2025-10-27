package com.example.taskmanager.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Task Manager API",
                version = "1.0.0",
                description = "REST API for managing tasks with full CRUD operations"
        )
)
public class OpenApiConfig {}
