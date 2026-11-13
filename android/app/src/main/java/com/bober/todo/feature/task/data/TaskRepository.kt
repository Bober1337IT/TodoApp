package com.bober.todo.feature.task.data

import com.bober.todo.feature.task.data.remote.dto.TaskDto

interface TaskRepository {

    suspend fun listTasks(): Result<List<TaskDto>>

    suspend fun createTask(
        title: String,
        description: String? = null,
        dueDate: String? = null,
        priority: String,
    ): Result<TaskDto>

    suspend fun updateTask(
        id: String,
        title: String,
        description: String? = null,
        dueDate: String? = null,
        priority: String,
        status: String,
    ): Result<TaskDto>

    suspend fun updateTaskStatus(
        id: String,
        status: String,
    ): Result<TaskDto>

    suspend fun deleteTask(id: String): Result<Unit>
}
