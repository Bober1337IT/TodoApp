package com.bober.todo.feature.task.presentation.form

import com.bober.todo.feature.task.data.remote.dto.TaskDto

data class TaskFormUiState(
    val title: String = "",
    val description: String = "",
    val dueDate: String = "",
    val priority: String = "MEDIUM",
    val status: String = "OPEN",
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val refreshedTasks: List<TaskDto>? = null,
    val errorMessage: String? = null,
    val titleError: String? = null,
)
