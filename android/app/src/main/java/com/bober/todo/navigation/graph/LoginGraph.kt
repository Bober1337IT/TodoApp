package com.bober.todo.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bober.todo.feature.auth.presentation.login.LoginScreen
import com.bober.todo.navigation.Screen

fun NavGraphBuilder.loginNavGraph(
    navController: NavController,
) {
    composable(Screen.LoginScreen.route) {
        LoginScreen(
            onNavigateToRegister = {
                navController.navigate(Screen.RegisterScreen.route)
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
