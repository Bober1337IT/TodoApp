package com.bober.todoapp.domain.dto;

import com.bober.todoapp.domain.entity.TaskPriority;
import com.bober.todoapp.domain.entity.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public record TaskDTO(

        UUID id,
        String name,
        String description,
        LocalDate dueDate,
        TaskPriority priority,
        TaskStatus status
) {
}
