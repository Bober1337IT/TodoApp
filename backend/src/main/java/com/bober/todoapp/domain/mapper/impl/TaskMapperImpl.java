package com.bober.todoapp.domain.mapper.impl;

import com.bober.todoapp.domain.CreateTaskRequest;
import com.bober.todoapp.domain.dto.CreateTaskRequestDTO;
import com.bober.todoapp.domain.dto.TaskDTO;
import com.bober.todoapp.domain.entity.Task;
import com.bober.todoapp.domain.mapper.TaskMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public CreateTaskRequest fromDTO(CreateTaskRequestDTO dto) {
        return new CreateTaskRequest(
                dto.title(),
                dto.description(),
                dto.dueDate(),
                dto.priority()
        );
    }

    @Override
    public TaskDTO toDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus()
        );
    }
}
