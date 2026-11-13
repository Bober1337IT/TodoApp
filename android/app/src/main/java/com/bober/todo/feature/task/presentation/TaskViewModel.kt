package com.bober.todo.feature.task.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bober.todo.feature.task.data.TaskRepository
import com.bober.todo.feature.task.data.remote.dto.TaskDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private var skipNextResumeLoad = false
    private var pendingDeleteResetJob: Job? = null

    fun loadTasksOnResume() {
        if (skipNextResumeLoad) {
            skipNextResumeLoad = false
            return
        }
        loadTasks(clearWhileLoading = true)
    }

    fun replaceTasks(tasks: List<TaskDto>) {
        skipNextResumeLoad = true
        clearPendingDelete()
        _uiState.update {
            it.copy(tasks = tasks, isLoading = false, errorMessage = null)
        }
    }

    fun loadTasks(clearWhileLoading: Boolean = true) {
        clearPendingDelete()
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                pendingDeleteTaskId = null,
                tasks = if (clearWhileLoading) emptyList() else it.tasks,
            )
        }
        viewModelScope.launch {
            taskRepository.listTasks()
                .onSuccess { tasks ->
                    _uiState.update {
                        it.copy(tasks = tasks, isLoading = false, errorMessage = null)
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

    fun onToggleStatus(task: TaskDto) {
        if (_uiState.value.updatingTaskId != null) return

        val newStatus = if (task.status == STATUS_COMPLETE) {
            STATUS_OPEN
        } else {
            STATUS_COMPLETE
        }

        clearPendingDelete()
        _uiState.update {
            it.copy(
                updatingTaskId = task.id,
                pendingDeleteTaskId = null,
                errorMessage = null,
            )
        }
        viewModelScope.launch {
            taskRepository.updateTaskStatus(id = task.id, status = newStatus)
                .onSuccess { updated ->
                    _uiState.update { state ->
                        state.copy(
                            tasks = state.tasks.map { current ->
                                if (current.id == updated.id) updated else current
                            },
                            updatingTaskId = null,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            updatingTaskId = null,
                            errorMessage = error.message ?: "Something went wrong",
                        )
                    }
                }
        }
    }

    fun onDeleteClick(taskId: String) {
        val state = _uiState.value
        if (state.updatingTaskId != null) return

        if (state.pendingDeleteTaskId == taskId) {
            clearPendingDelete()
            _uiState.update {
                it.copy(
                    pendingDeleteTaskId = null,
                    updatingTaskId = taskId,
                    errorMessage = null,
                )
            }
            viewModelScope.launch {
                taskRepository.deleteTask(taskId)
                    .onSuccess {
                        _uiState.update { current ->
                            current.copy(
                                tasks = current.tasks.filter { it.id != taskId },
                                updatingTaskId = null,
                                errorMessage = null,
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                updatingTaskId = null,
                                errorMessage = error.message ?: "Something went wrong",
                            )
                        }
                    }
            }
            return
        }

        pendingDeleteResetJob?.cancel()
        _uiState.update { it.copy(pendingDeleteTaskId = taskId, errorMessage = null) }
        pendingDeleteResetJob = viewModelScope.launch {
            delay(PENDING_DELETE_TIMEOUT_MS)
            _uiState.update { current ->
                if (current.pendingDeleteTaskId == taskId) {
                    current.copy(pendingDeleteTaskId = null)
                } else {
                    current
                }
            }
        }
    }

    private fun clearPendingDelete() {
        pendingDeleteResetJob?.cancel()
        pendingDeleteResetJob = null
    }

    private companion object {
        const val STATUS_OPEN = "OPEN"
        const val STATUS_COMPLETE = "COMPLETE"
        const val PENDING_DELETE_TIMEOUT_MS = 3_000L
    }
}
