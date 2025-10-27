package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void testCreateTask() throws Exception {
        TaskRequest request = new TaskRequest("Test Task", "Test Description", TaskStatus.TODO);
        TaskResponse response = new TaskResponse(1L, "Test Task", "Test Description", TaskStatus.TODO, LocalDateTime.now());
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(response);
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("TODO"));
        verify(taskService, times(1)).createTask(any(TaskRequest.class));
    }

    @Test
    void testCreateTaskWithEmptyTitle() throws Exception {
        TaskRequest invalidRequest = new TaskRequest("", "Test Description", TaskStatus.TODO);
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors[0].field").value("title"));
        verify(taskService, never()).createTask(any());
    }

    @Test
    void testGetTaskById() throws Exception {
        TaskResponse response = new TaskResponse(1L, "Test Task", "Description", TaskStatus.IN_PROGRESS, LocalDateTime.now());
        when(taskService.getTask(1L)).thenReturn(response);
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
        verify(taskService, times(1)).getTask(1L);
    }

    @Test
    void testGetAllTasks() throws Exception {
        TaskResponse task1 = new TaskResponse(1L, "Task 1", "Desc 1", TaskStatus.TODO, LocalDateTime.now());
        TaskResponse task2 = new TaskResponse(2L, "Task 2", "Desc 2", TaskStatus.DONE, LocalDateTime.now());
        when(taskService.getAllTasks()).thenReturn(List.of(task1, task2));
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskRequest request = new TaskRequest("Updated Task", "Updated Desc", TaskStatus.DONE);
        TaskResponse response = new TaskResponse(1L, "Updated Task", "Updated Desc", TaskStatus.DONE, LocalDateTime.now());
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(response);
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));
        verify(taskService, times(1)).updateTask(eq(1L), any(TaskRequest.class));
    }

    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(1L);
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
        verify(taskService, times(1)).deleteTask(1L);
    }
}