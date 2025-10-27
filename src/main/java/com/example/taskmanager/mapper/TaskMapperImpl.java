package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public TaskResponse entityToDTO(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getCreatedAt());
    }

    @Override
    public Task dtoToEntity(TaskRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setStatus(taskRequest.status());
        return task;
    }

    @Override
    public void updateEntityFromDto(TaskRequest taskRequest, Task task) {
        if (taskRequest.title() != null) {
            task.setTitle(taskRequest.title());
        }
        if (taskRequest.description() != null) {
            task.setDescription(taskRequest.description());
        }
        if (taskRequest.status() != null) {
            task.setStatus(taskRequest.status());
        }
    }
}