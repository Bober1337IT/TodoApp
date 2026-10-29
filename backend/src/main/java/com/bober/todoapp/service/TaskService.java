package com.bober.todoapp.service;

import com.bober.todoapp.domain.CreateTaskRequest;
import com.bober.todoapp.domain.UpdateTaskRequest;
import com.bober.todoapp.domain.UpdateTaskStatusRequest;
import com.bober.todoapp.domain.entity.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    Task createTask(CreateTaskRequest request);

    List<Task> listTasks();

    void deleteTaskById(UUID taskId);

    Task updateTaskById(UUID taskId, UpdateTaskRequest request);

    Task updateTaskStatusById(UUID taskId, UpdateTaskStatusRequest request);
}
