package com.bober.todo.feature.task.data.remote.dto

data class CreateTaskRequestDto(
    val title: String,
    val description: String? = null,
    val dueDate: String? = null,
    val priority: String,
)
