package com.bober.todoapp.service;

import com.bober.todoapp.domain.CreateTaskRequest;
import com.bober.todoapp.domain.entity.Task;

public interface TaskService {

    Task createTask(CreateTaskRequest request);

}
