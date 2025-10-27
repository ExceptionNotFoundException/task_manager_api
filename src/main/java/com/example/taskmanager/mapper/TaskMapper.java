package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.model.Task;

public interface TaskMapper {

    TaskResponse entityToDTO(Task task);

    Task dtoToEntity(TaskRequest taskRequest);

    void updateEntityFromDto(TaskRequest taskRequest, Task task);


}
