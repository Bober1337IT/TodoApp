package com.bober.todo.feature.task.presentation

import com.bober.todo.feature.task.data.remote.dto.TaskDto

data class TaskUiState(
    val tasks: List<TaskDto> = emptyList(),
    val isLoading: Boolean = true,
    val updatingTaskId: String? = null,
    val errorMessage: String? = null,
)
