package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    private TaskServiceImpl taskService;

    private Task testTask;
    private TaskResponse testTaskResponse;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository, taskMapper);
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.TODO);
        testTaskResponse = new TaskResponse(1L, "Test Task", "Test Description", TaskStatus.TODO, null);
    }

    @Test
    void testCreateTask() {
        TaskRequest request = new TaskRequest("Test Task", "Test Description", TaskStatus.TODO);
        when(taskMapper.dtoToEntity(request)).thenReturn(testTask);
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskMapper.entityToDTO(testTask)).thenReturn(testTaskResponse);
        TaskResponse result = taskService.createTask(request);
        assertNotNull(result);
        assertEquals("Test Task", result.title());
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void testGetTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskMapper.entityToDTO(testTask)).thenReturn(testTaskResponse);
        TaskResponse result = taskService.getTask(1L);
        assertEquals(1L, result.id());
        assertEquals("Test Task", result.title());
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getTask(999L);
        });
    }

    @Test
    void testGetAllTasks() {
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        TaskResponse response2 = new TaskResponse(2L, "Task 2", null, null, null);
        when(taskRepository.findAll()).thenReturn(List.of(testTask, task2));
        when(taskMapper.entityToDTO(testTask)).thenReturn(testTaskResponse);
        when(taskMapper.entityToDTO(task2)).thenReturn(response2);
        List<TaskResponse> result = taskService.getAllTasks();
        assertEquals(2, result.size());
        assertEquals("Test Task", result.get(0).title());
        assertEquals("Task 2", result.get(1).title());
    }

    @Test
    void testUpdateTask() {
        TaskRequest request = new TaskRequest("Updated Task", "Updated Desc", TaskStatus.DONE);
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Desc");
        updatedTask.setStatus(TaskStatus.DONE);
        TaskResponse updatedResponse = new TaskResponse(1L, "Updated Task", "Updated Desc", TaskStatus.DONE, null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(testTask)).thenReturn(updatedTask);
        when(taskMapper.entityToDTO(updatedTask)).thenReturn(updatedResponse);
        TaskResponse result = taskService.updateTask(1L, request);
        assertEquals("Updated Task", result.title());
        assertEquals(TaskStatus.DONE, result.status());
    }

    @Test
    void testDeleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doNothing().when(taskRepository).delete(testTask);
        taskService.deleteTask(1L);
        verify(taskRepository, times(1)).delete(testTask);
    }
}