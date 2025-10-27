package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "API for managing tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Get task by ID", description = "Returns a single task by its ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Task found"), @ApiResponse(responseCode = "404", description = "Task not found")})
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@Parameter(description = "ID of the task to retrieve") @PathVariable Long id){
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @Operation(summary = "Get all tasks", description = "Returns list of all tasks")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(){
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @Operation(summary = "Create new task", description = "Creates a new task and returns it")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Task created successfully"), @ApiResponse(responseCode = "400", description = "Invalid input data")})
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Parameter(description = "Task data to create") @Valid @RequestBody TaskRequest taskRequest){
        TaskResponse created = taskService.createTask(taskRequest);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @Operation(summary = "Update task", description = "Updates an existing task")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Task updated successfully"), @ApiResponse(responseCode = "400", description = "Invalid input data"), @ApiResponse(responseCode = "404", description = "Task not found")})
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@Parameter(description = "ID of the task to update") @PathVariable Long id, @Parameter(description = "Updated task data") @Valid @RequestBody TaskRequest taskRequest){
        return ResponseEntity.ok(taskService.updateTask(id, taskRequest));
    }

    @Operation(summary = "Delete task", description = "Deletes a task by ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Task deleted successfully"), @ApiResponse(responseCode = "404", description = "Task not found")})
    @DeleteMapping("/{id}")
    public ResponseEntity<TaskResponse> deleteTask(@Parameter(description = "ID of the task to delete") @PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
