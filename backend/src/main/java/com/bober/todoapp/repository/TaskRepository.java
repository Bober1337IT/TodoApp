package com.bober.todoapp.repository;

import com.bober.todoapp.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findAllByUserIdOrderByCreatedAsc(UUID userId);
    Optional<Task> findByIdAndUserId(UUID id, UUID userId);

}
