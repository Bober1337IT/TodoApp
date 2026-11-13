package com.bober.todo.feature.task.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bober.todo.feature.task.data.remote.dto.TaskDto
import com.bober.todo.ui.theme.TodoappTheme

@Composable
fun TaskScreen(
    onAddTask: () -> Unit,
    onEditTask: (taskId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadTasksOnResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    TaskScreenContent(
        state = state,
        onRefresh = { viewModel.loadTasks(clearWhileLoading = false) },
        onToggleStatus = viewModel::onToggleStatus,
        onDeleteTask = viewModel::onDeleteClick,
        onAddTask = onAddTask,
        onEditTask = onEditTask,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreenContent(
    state: TaskUiState,
    onRefresh: () -> Unit,
    onToggleStatus: (TaskDto) -> Unit,
    onDeleteTask: (taskId: String) -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (taskId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Text("+")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
        ) {
            Text(
                text = "All tasks",
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (state.errorMessage != null && state.tasks.isNotEmpty()) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            PullToRefreshBox(
                isRefreshing = state.isLoading && state.tasks.isNotEmpty(),
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                when {
                    state.isLoading && state.tasks.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    state.errorMessage != null && state.tasks.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = state.errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = onRefresh) {
                                Text("Retry")
                            }
                        }
                    }

                    state.tasks.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No tasks yet. Tap + to create one.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(
                                items = state.tasks,
                                key = { it.id },
                            ) { task ->
                                TaskRow(
                                    task = task,
                                    isUpdating = state.updatingTaskId == task.id,
                                    isPendingDelete = state.pendingDeleteTaskId == task.id,
                                    onToggleStatus = { onToggleStatus(task) },
                                    onEdit = { onEditTask(task.id) },
                                    onDelete = { onDeleteTask(task.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(
    task: TaskDto,
    isUpdating: Boolean,
    isPendingDelete: Boolean,
    onToggleStatus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isComplete = task.status == "COMPLETE"
    val titleStyle: TextStyle = MaterialTheme.typography.titleMedium.let { style ->
        if (isComplete) {
            style.copy(
                textDecoration = TextDecoration.LineThrough,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            style
        }
    }
    val cardColors = if (isPendingDelete) {
        CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        )
    } else {
        CardDefaults.outlinedCardColors()
    }

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = cardColors,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            if (isUpdating) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                }
            } else {
                Checkbox(
                    checked = isComplete,
                    onCheckedChange = { onToggleStatus() },
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = titleStyle,
                )

                if (!task.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (!task.dueDate.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Due ${task.dueDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.width(IntrinsicSize.Max),
            ) {
                Text(
                    text = task.priority,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TaskActionButton(
                    text = "Edit",
                    enabled = !isUpdating,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    borderColor = MaterialTheme.colorScheme.outline,
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth(),
                )
                TaskActionButton(
                    text = if (isPendingDelete) "Confirm" else "Delete",
                    enabled = !isUpdating,
                    contentColor = MaterialTheme.colorScheme.onError,
                    borderColor = MaterialTheme.colorScheme.error,
                    containerColor = MaterialTheme.colorScheme.error,
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun TaskActionButton(
    text: String,
    enabled: Boolean,
    contentColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
) {
    val shape = RoundedCornerShape(6.dp)
    val resolvedContent = if (enabled) contentColor else contentColor.copy(alpha = 0.38f)
    val resolvedBorder = if (enabled) borderColor else borderColor.copy(alpha = 0.38f)
    val resolvedContainer = if (enabled) {
        containerColor
    } else {
        containerColor.copy(alpha = 0.38f)
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = resolvedContent,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(shape)
            .background(resolvedContainer)
            .border(width = 1.dp, color = resolvedBorder, shape = shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun TaskPreviewList() {
    TodoappTheme {
        TaskScreenContent(
            state = TaskUiState(
                isLoading = false,
                tasks = listOf(
                    TaskDto(
                        id = "1",
                        title = "Buy milk",
                        description = "From the store",
                        dueDate = "2026-11-20",
                        priority = "MEDIUM",
                        status = "OPEN",
                    ),
                    TaskDto(
                        id = "2",
                        title = "Write code",
                        priority = "HIGH",
                        status = "COMPLETE",
                    ),
                ),
            ),
            onRefresh = {},
            onToggleStatus = {},
            onDeleteTask = {},
            onAddTask = {},
            onEditTask = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskPreviewEmpty() {
    TodoappTheme {
        TaskScreenContent(
            state = TaskUiState(isLoading = false),
            onRefresh = {},
            onToggleStatus = {},
            onDeleteTask = {},
            onAddTask = {},
            onEditTask = {},
        )
    }
}
