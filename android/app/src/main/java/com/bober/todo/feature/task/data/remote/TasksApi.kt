package com.bober.todo.feature.task.data.remote

import com.bober.todo.feature.task.data.remote.dto.TaskDto
import retrofit2.http.GET

interface TasksApi {

    @GET("api/v1/tasks")
    suspend fun listTasks(): List<TaskDto>
}
