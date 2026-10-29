package com.bober.todoapp.service;

import com.bober.todoapp.domain.CreateTaskRequest;
import com.bober.todoapp.domain.UpdatedTaskRequest;
import com.bober.todoapp.domain.entity.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    Task createTask(CreateTaskRequest request);

    List<Task> listTasks();

    void deleteTaskById(UUID taskId);

    Task updateTaskById(UUID taskId, UpdatedTaskRequest request);
}
