package com.bober.todoapp.controller;

import com.bober.todoapp.domain.CreateTaskRequest;
import com.bober.todoapp.domain.UpdatedTaskRequest;
import com.bober.todoapp.domain.dto.CreateTaskRequestDTO;
import com.bober.todoapp.domain.dto.TaskDTO;
import com.bober.todoapp.domain.dto.UpdatedTaskRequestDTO;
import com.bober.todoapp.domain.entity.Task;
import com.bober.todoapp.domain.mapper.TaskMapper;
import com.bober.todoapp.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @Valid
            @RequestBody
            CreateTaskRequestDTO createTaskRequestDTO
    ) {
        CreateTaskRequest createTaskRequest = taskMapper.fromDTO(createTaskRequestDTO);
        Task task = taskService.createTask(createTaskRequest);
        TaskDTO createdTaskDTO = taskMapper.toDTO(task);
        return new ResponseEntity<>(createdTaskDTO, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<TaskDTO>> listTasks(){
        List<Task> tasks = taskService.listTasks();
        List<TaskDTO> tasksDTO = tasks.stream().map(taskMapper::toDTO).toList();
        return ResponseEntity.ok(tasksDTO);
    }

    @DeleteMapping(path = "/{taskId}")
    public ResponseEntity<Void> deleteTaskById(
            @PathVariable
            UUID taskId
    ){
        taskService.deleteTaskById(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/{taskId}")
    public ResponseEntity<TaskDTO> updateTaskById(
            @PathVariable
            UUID taskId,
            @Valid
            @RequestBody
            UpdatedTaskRequestDTO updatedTaskRequestDTO
    ){
        UpdatedTaskRequest updatedTaskRequest = taskMapper.fromDTO(updatedTaskRequestDTO);
        Task task = taskService.updateTaskById(taskId, updatedTaskRequest);
        TaskDTO taskDTO = taskMapper.toDTO(task);
        return ResponseEntity.ok(taskDTO);
    }
}
