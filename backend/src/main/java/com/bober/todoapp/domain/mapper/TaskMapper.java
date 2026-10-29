package com.bober.todoapp.domain.mapper;

import com.bober.todoapp.domain.CreateTaskRequest;
import com.bober.todoapp.domain.UpdatedTaskRequest;
import com.bober.todoapp.domain.dto.CreateTaskRequestDTO;
import com.bober.todoapp.domain.dto.TaskDTO;
import com.bober.todoapp.domain.dto.UpdatedTaskRequestDTO;
import com.bober.todoapp.domain.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    CreateTaskRequest fromDTO(CreateTaskRequestDTO dto);

    UpdatedTaskRequest fromDTO(UpdatedTaskRequestDTO dto);

    TaskDTO toDTO(Task task);
}
