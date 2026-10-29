package com.bober.todoapp.domain;

import com.bober.todoapp.domain.entity.TaskStatus;

public record UpdateTaskStatusRequest(
        TaskStatus status
) {
}
