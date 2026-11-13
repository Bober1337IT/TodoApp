package com.bober.todo.feature.task.presentation

import com.bober.todo.feature.task.data.remote.dto.TaskDto

data class TaskUiState(
    val tasks: List<TaskDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)