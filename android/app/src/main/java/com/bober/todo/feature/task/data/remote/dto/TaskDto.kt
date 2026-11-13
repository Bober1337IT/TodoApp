package com.bober.todo.feature.task.data.remote.dto

data class TaskDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val dueDate: String? = null,
    val priority: String,
    val status: String,
)
