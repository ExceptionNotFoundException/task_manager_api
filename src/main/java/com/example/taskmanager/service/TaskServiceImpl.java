package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.exception.InternalServerException;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.exception.WrongRequestException;
import com.example.taskmanager.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Transactional
    @Override
    public TaskResponse getTask(Long id) {
        if (id == null || id <= 0) {
            throw new WrongRequestException("Invalid task ID: " + id);
        }
        try {
            Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
            return taskMapper.entityToDTO(task);
        } catch (DataAccessException e) {
            throw new InternalServerException("Failed to retrieve task due to database error", e);
        }
    }

    @Transactional
    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream().map(taskMapper::entityToDTO).toList();
    }

    @Transactional
    @Override
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task task = taskMapper.dtoToEntity(taskRequest);
        Task savedTask = taskRepository.save(task);
        return taskMapper.entityToDTO(savedTask);
    }

    @Transactional
    @Override
    public TaskResponse updateTask(Long id, TaskRequest taskRequest) {
        try {
            Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
            taskMapper.updateEntityFromDto(taskRequest, task);
            Task updatedTask = taskRepository.save(task);
            return taskMapper.entityToDTO(updatedTask);
        } catch (DataAccessException e) {
            throw new InternalServerException("Failed to update task due to database error", e);
        }
    }

    @Transactional
    @Override
    public void deleteTask(Long id){
        try {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
            taskRepository.delete(task);
        } catch (DataAccessException e) {
            throw new InternalServerException("Failed to delete task due to database error", e);
        }
    }
}
