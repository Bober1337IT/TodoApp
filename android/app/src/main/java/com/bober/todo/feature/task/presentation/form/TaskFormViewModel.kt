package com.bober.todo.feature.task.presentation.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bober.todo.feature.task.data.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val taskId: String? = savedStateHandle[TASK_ID_ARG]

    private val _uiState = MutableStateFlow(
        TaskFormUiState(isEditMode = taskId != null, isLoading = taskId != null),
    )
    val uiState: StateFlow<TaskFormUiState> = _uiState.asStateFlow()

    init {
        if (taskId != null) {
            loadTask(taskId)
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, titleError = null, errorMessage = null) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description, errorMessage = null) }
    }

    fun onDueDateChange(dueDate: String) {
        _uiState.update { it.copy(dueDate = dueDate, errorMessage = null) }
    }

    fun onPriorityChange(priority: String) {
        _uiState.update { it.copy(priority = priority, errorMessage = null) }
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null, titleError = null) }
        viewModelScope.launch {
            val dueDate = state.dueDate.trim().ifBlank { null }
            val description = state.description.trim().ifBlank { null }
            val result = if (taskId == null) {
                taskRepository.createTask(
                    title = state.title.trim(),
                    description = description,
                    dueDate = dueDate,
                    priority = state.priority,
                )
            } else {
                taskRepository.updateTask(
                    id = taskId,
                    title = state.title.trim(),
                    description = description,
                    dueDate = dueDate,
                    priority = state.priority,
                    status = state.status,
                )
            }

            result
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Something went wrong",
                        )
                    }
                }
                .onSuccess {
                    taskRepository.listTasks()
                        .onSuccess { tasks ->
                            _uiState.update {
                                it.copy(
                                    isSaving = false,
                                    isSuccess = true,
                                    refreshedTasks = tasks,
                                )
                            }
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(
                                    isSaving = false,
                                    errorMessage = error.message
                                        ?: "Saved, but failed to reload tasks",
                                )
                            }
                        }
                }
        }
    }

    private fun loadTask(id: String) {
        viewModelScope.launch {
            taskRepository.listTasks()
                .onSuccess { tasks ->
                    val task = tasks.find { it.id == id }
                    if (task == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Task not found",
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                title = task.title,
                                description = task.description.orEmpty(),
                                dueDate = task.dueDate.orEmpty(),
                                priority = task.priority,
                                status = task.status,
                                isLoading = false,
                                errorMessage = null,
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Something went wrong",
                        )
                    }
                }
        }
    }

    companion object {
        const val TASK_ID_ARG = "taskId"
        val priorities = listOf("HIGH", "MEDIUM", "LOW")
    }
}
