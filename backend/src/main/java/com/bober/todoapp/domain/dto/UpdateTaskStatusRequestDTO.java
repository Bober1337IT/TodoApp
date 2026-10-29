package com.bober.todoapp.domain.dto;

import com.bober.todoapp.domain.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequestDTO(
        @NotNull
        TaskStatus status
) {
}
