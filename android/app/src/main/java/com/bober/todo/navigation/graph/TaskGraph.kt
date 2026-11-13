package com.bober.todo.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bober.todo.navigation.Screen
import com.bober.todo.feature.task.presentation.TaskScreen


fun NavGraphBuilder.taskNavGraph(
    navController: NavController,
) {
    composable(Screen.TaskScreen.route) {
        TaskScreen()
    }
}
