package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import java.util.List;

public interface TaskService {

    TaskResponse getTask(Long id);

    List<TaskResponse> getAllTasks();

    TaskResponse createTask(TaskRequest taskRequest);

    TaskResponse updateTask(Long id, TaskRequest request);

    void deleteTask(Long id);

}
