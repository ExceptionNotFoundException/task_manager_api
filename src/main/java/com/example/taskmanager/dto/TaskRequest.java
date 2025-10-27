package com.example.taskmanager.dto;

import com.example.taskmanager.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request for creating or updating a task")
public record TaskRequest(
        @Schema(description = "Task title", example = "Write a client for an external weather API")
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
        String title,

        @Schema(description = "Task description", example = "Create a WeatherService that makes a request to a Public API by the name of a city and returns the current temperature")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @Schema(description = "Task status", example = "IN_PROGRESS")
        @NotNull(message = "Status is required")
        TaskStatus status
) {}