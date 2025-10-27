package com.example.taskmanager.dto;

import com.example.taskmanager.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Task response data")
public record TaskResponse(
        @Schema(description = "Task ID", example = "1")
        Long id,
        @Schema(description = "Task title", example = "Write a client for an external weather API")
        String title,
        @Schema(description = "Task description", example = "Create a WeatherService that makes a request to a Public API by the name of a city and returns the current temperature")
        String description,
        @Schema(description = "Task status", example = "IN_PROGRESS")
        TaskStatus status,
        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt

) {}