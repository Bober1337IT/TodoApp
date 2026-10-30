package com.bober.todoapp.service.impl;

import com.bober.todoapp.domain.CreateTaskRequest;
import com.bober.todoapp.domain.UpdateTaskRequest;
import com.bober.todoapp.domain.UpdateTaskStatusRequest;
import com.bober.todoapp.domain.entity.Task;
import com.bober.todoapp.domain.entity.TaskStatus;
import com.bober.todoapp.exception.TaskNotFoundException;
import com.bober.todoapp.repository.TaskRepository;
import com.bober.todoapp.service.TaskService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(CreateTaskRequest request) {
        Instant now = Instant.now();

        Task task = new Task(
                null,
                request.title(),
                request.description(),
                request.dueDate(),
                TaskStatus.OPEN,
                request.priority(),
                now,
                now
        );

        return taskRepository.save(task);
    }

    @Override
    public List<Task> listTasks(){
        return taskRepository.findAll(Sort.by(Sort.Direction.ASC, "created"));
    }

    @Override
    public void deleteTaskById(UUID taskId) {
        taskRepository.deleteById(taskId);
    }

    @Override
    public Task updateTaskById(UUID taskId, UpdateTaskRequest request) {

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setPriority(request.priority());
        task.setStatus(request.status());
        task.setUpdated(Instant.now());

        return taskRepository.save(task);
    }

    @Override
    public Task updateTaskStatusById(UUID taskId, UpdateTaskStatusRequest request) {

        Task task = taskRepository.findById(taskId).orElseThrow();

        task.setStatus(request.status());
        task.setUpdated(Instant.now());

        return taskRepository.save(task);
    }
}