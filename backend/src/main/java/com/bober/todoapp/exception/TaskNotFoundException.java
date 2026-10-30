package com.bober.todoapp.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TaskNotFoundException extends RuntimeException {

    private final UUID taskID;

    public TaskNotFoundException(UUID taskID) {
        super(String.format("Task with ID '%s' does not exists", taskID));
        this.taskID = taskID;
    }
}
