package com.bober.todoapp.domain.mapper;

import com.bober.todoapp.domain.CreateTaskRequest;
import com.bober.todoapp.domain.UpdateTaskRequest;
import com.bober.todoapp.domain.UpdateTaskStatusRequest;
import com.bober.todoapp.domain.dto.CreateTaskRequestDTO;
import com.bober.todoapp.domain.dto.TaskDTO;
import com.bober.todoapp.domain.dto.UpdateTaskRequestDTO;
import com.bober.todoapp.domain.dto.UpdateTaskStatusRequestDTO;
import com.bober.todoapp.domain.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    CreateTaskRequest fromDTO(CreateTaskRequestDTO dto);

    UpdateTaskRequest fromDTO(UpdateTaskRequestDTO dto);

    UpdateTaskStatusRequest fromDTO(UpdateTaskStatusRequestDTO dto);

    TaskDTO toDTO(Task task);
}
