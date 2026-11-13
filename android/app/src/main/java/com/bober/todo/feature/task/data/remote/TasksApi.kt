package com.bober.todo.feature.task.data.remote

import com.bober.todo.feature.task.data.remote.dto.CreateTaskRequestDto
import com.bober.todo.feature.task.data.remote.dto.TaskDto
import com.bober.todo.feature.task.data.remote.dto.UpdateTaskRequestDto
import com.bober.todo.feature.task.data.remote.dto.UpdateTaskStatusRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TasksApi {

    @GET("api/v1/tasks")
    suspend fun listTasks(): List<TaskDto>

    @POST("api/v1/tasks")
    suspend fun createTask(@Body body: CreateTaskRequestDto): TaskDto

    @PUT("api/v1/tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Body body: UpdateTaskRequestDto,
    ): TaskDto

    @PATCH("api/v1/tasks/{id}/status")
    suspend fun updateTaskStatus(
        @Path("id") id: String,
        @Body body: UpdateTaskStatusRequestDto,
    ): TaskDto

    @DELETE("api/v1/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String)
}
