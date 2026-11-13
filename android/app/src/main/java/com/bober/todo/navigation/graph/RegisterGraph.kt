package com.bober.todo.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bober.todo.feature.auth.presentation.register.RegisterScreen
import com.bober.todo.navigation.Screen

fun NavGraphBuilder.registerNavGraph(
    navController: NavController,
) {
    composable(Screen.RegisterScreen.route) {
        RegisterScreen(
            onNavigateToLogin = {
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(Screen.LoginScreen.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            onNavigateToTask = {
                navController.navigate(Screen.TaskScreen.route) {
                    popUpTo(Screen.LoginScreen.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )
    }
}
