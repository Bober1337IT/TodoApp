package com.bober.todoapp.service;

import com.bober.todoapp.domain.CreateTaskRequest;
import com.bober.todoapp.domain.entity.Task;

import java.util.List;

public interface TaskService {

    Task createTask(CreateTaskRequest request);

    List<Task> listTasks();

}
