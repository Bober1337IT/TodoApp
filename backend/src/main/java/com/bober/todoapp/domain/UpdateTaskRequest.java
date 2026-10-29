package com.bober.todoapp.domain;

import com.bober.todoapp.domain.entity.TaskPriority;
import com.bober.todoapp.domain.entity.TaskStatus;

import java.time.LocalDate;

public record UpdateTaskRequest(
        String title,
        String description,
        LocalDate dueDate,
        TaskPriority priority,
        TaskStatus status
) {

}
