package com.bober.todo.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bober.todo.feature.splash.presentation.SplashScreen
import com.bober.todo.navigation.Screen

fun NavGraphBuilder.splashNavGraph(
    navController: NavController,
) {
    composable(Screen.SplashScreen.route) {
        SplashScreen(
            onNavigateToLogin = {
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onNavigateToTasks = {
                navController.navigate(Screen.TaskScreen.route) {
                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
    }
}
