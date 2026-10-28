package com.bober.todoapp.domain.mapper;

import com.bober.todoapp.domain.CreateTaskRequest;
import com.bober.todoapp.domain.dto.CreateTaskRequestDTO;
import com.bober.todoapp.domain.dto.TaskDTO;
import com.bober.todoapp.domain.entity.Task;

// Later use Mapstruct library
public interface TaskMapper {

    CreateTaskRequest fromDTO(CreateTaskRequestDTO dto);

    TaskDTO toDTO(Task task);
}
