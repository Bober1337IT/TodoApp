package com.bober.todo.feature.task.data

import com.bober.todo.core.network.SafeApiCall
import com.bober.todo.feature.task.data.remote.TasksApi
import com.bober.todo.feature.task.data.remote.dto.CreateTaskRequestDto
import com.bober.todo.feature.task.data.remote.dto.TaskDto
import com.bober.todo.feature.task.data.remote.dto.UpdateTaskRequestDto
import com.bober.todo.feature.task.data.remote.dto.UpdateTaskStatusRequestDto
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val tasksApi: TasksApi,
    private val safeApiCall: SafeApiCall,
) : TaskRepository {

    override suspend fun listTasks(): Result<List<TaskDto>> {
        return safeApiCall.run(statusMessages = taskStatusMessages) {
            tasksApi.listTasks()
        }
    }

    override suspend fun createTask(
        title: String,
        description: String?,
        dueDate: String?,
        priority: String,
    ): Result<TaskDto> {
        return safeApiCall.run(statusMessages = taskStatusMessages) {
            tasksApi.createTask(
                CreateTaskRequestDto(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    priority = priority,
                )
            )
        }
    }

    override suspend fun updateTask(
        id: String,
        title: String,
        description: String?,
        dueDate: String?,
        priority: String,
        status: String,
    ): Result<TaskDto> {
        return safeApiCall.run(statusMessages = taskStatusMessages) {
            tasksApi.updateTask(
                id = id,
                body = UpdateTaskRequestDto(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    priority = priority,
                    status = status,
                )
            )
        }
    }

    override suspend fun updateTaskStatus(
        id: String,
        status: String,
    ): Result<TaskDto> {
        return safeApiCall.run(statusMessages = taskStatusMessages) {
            tasksApi.updateTaskStatus(
                id = id,
                body = UpdateTaskStatusRequestDto(
                    status = status,
                )
            )
        }
    }

    override suspend fun deleteTask(id: String): Result<Unit> {
        return safeApiCall.run(statusMessages = taskStatusMessages) {
            tasksApi.deleteTask(id = id)
        }
    }

    private companion object {
        val taskStatusMessages = mapOf(
            401 to "Session expired. Please sign in again.",
            404 to "Task not found",
        )
    }
}
