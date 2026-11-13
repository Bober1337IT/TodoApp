package com.bober.todo.feature.task.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bober.todo.feature.task.data.remote.dto.TaskDto
import com.bober.todo.ui.theme.TodoappTheme

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    TaskScreenContent(
        state = state,
        onRetry = viewModel::loadTasks,
        modifier = modifier,
    )
}

@Composable
fun TaskScreenContent(
    state: TaskUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
    ) {
        Text(
            text = "Tasks",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))

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
                    Button(onClick = onRetry) {
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
                        text = "No tasks yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    items(
                        items = state.tasks,
                        key = { it.id },
                    ) { task ->
                        TaskRow(task = task)
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(
    task: TaskDto,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${task.status} · ${task.priority}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (!task.description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskPreviewList() {
    TodoappTheme {
        TaskScreenContent(
            state = TaskUiState(
                tasks = listOf(
                    TaskDto(
                        id = "1",
                        title = "Buy milk",
                        description = "From the store",
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
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskPreviewEmpty() {
    TodoappTheme {
        TaskScreenContent(
            state = TaskUiState(),
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskPreviewLoading() {
    TodoappTheme {
        TaskScreenContent(
            state = TaskUiState(isLoading = true),
            onRetry = {},
        )
    }
}
