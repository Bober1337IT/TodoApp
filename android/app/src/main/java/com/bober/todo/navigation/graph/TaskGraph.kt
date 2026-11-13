package com.bober.todo.navigation.graph

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bober.todo.feature.task.presentation.TaskScreen
import com.bober.todo.feature.task.presentation.TaskViewModel
import com.bober.todo.feature.task.presentation.form.TaskFormScreen
import com.bober.todo.feature.task.presentation.form.TaskFormViewModel
import com.bober.todo.navigation.Screen

fun NavGraphBuilder.taskNavGraph(
    navController: NavController,
) {
    composable(Screen.TaskScreen.route) {
        TaskScreen(
            onAddTask = {
                navController.navigate(Screen.TaskAddScreen.route)
            },
            onEditTask = { taskId ->
                navController.navigate(Screen.TaskEditScreen.createRoute(taskId))
            },
        )
    }

    composable(Screen.TaskAddScreen.route) {
        val listEntry = remember(it) {
            navController.getBackStackEntry(Screen.TaskScreen.route)
        }
        val taskListViewModel: TaskViewModel = hiltViewModel(listEntry)

        TaskFormScreen(
            onNavigateBack = { navController.popBackStack() },
            onSaved = { tasks ->
                taskListViewModel.replaceTasks(tasks)
                navController.popBackStack()
            },
        )
    }

    composable(
        route = Screen.TaskEditScreen.route,
        arguments = listOf(
            navArgument(TaskFormViewModel.TASK_ID_ARG) {
                type = NavType.StringType
            },
        ),
    ) {
        val listEntry = remember(it) {
            navController.getBackStackEntry(Screen.TaskScreen.route)
        }
        val taskListViewModel: TaskViewModel = hiltViewModel(listEntry)

        TaskFormScreen(
            onNavigateBack = { navController.popBackStack() },
            onSaved = { tasks ->
                taskListViewModel.replaceTasks(tasks)
                navController.popBackStack()
            },
        )
    }
}
