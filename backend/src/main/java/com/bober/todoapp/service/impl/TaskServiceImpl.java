package com.bober.todoapp.service.impl;

import com.bober.todoapp.domain.CreateTaskRequest;
import com.bober.todoapp.domain.UpdateTaskRequest;
import com.bober.todoapp.domain.UpdateTaskStatusRequest;
import com.bober.todoapp.domain.entity.Task;
import com.bober.todoapp.domain.entity.TaskStatus;
import com.bober.todoapp.domain.entity.User;
import com.bober.todoapp.exception.TaskNotFoundException;
import com.bober.todoapp.repository.TaskRepository;
import com.bober.todoapp.repository.UserRepository;
import com.bober.todoapp.security.CurrentUserService;
import com.bober.todoapp.security.UserPrincipal;
import com.bober.todoapp.service.TaskService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public Task createTask(CreateTaskRequest request) {
        User user = requireCurrentUserEntity();
        Instant now = Instant.now();

        Task task = new Task(
                null,
                user,
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
    public List<Task> listTasks() {
        UUID userId = currentUserService.requireUser().id();
        return taskRepository.findAllByUserIdOrderByCreatedAsc(userId);
    }

    @Override
    public void deleteTaskById(UUID taskId) {
        Task task = requireOwnedTask(taskId);
        taskRepository.delete(task);
    }

    @Override
    public Task updateTaskById(UUID taskId, UpdateTaskRequest request) {
        Task task = requireOwnedTask(taskId);

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
        Task task = requireOwnedTask(taskId);

        task.setStatus(request.status());
        task.setUpdated(Instant.now());

        return taskRepository.save(task);
    }

    private User requireCurrentUserEntity() {
        UserPrincipal principal = currentUserService.requireUser();
        return userRepository.findById(principal.id())
                .orElseThrow(() -> new IllegalStateException("Authenticated user no longer exists"));
    }

    private Task requireOwnedTask(UUID taskId) {
        UUID userId = currentUserService.requireUser().id();
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }
}
