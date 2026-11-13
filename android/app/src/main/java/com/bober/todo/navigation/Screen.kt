package com.bober.todo.navigation

sealed class Screen(val route: String) {

    data object SplashScreen : Screen("splash")
    data object LoginScreen : Screen("login")
    data object RegisterScreen : Screen("register")
    data object TaskScreen : Screen("task")
    data object TaskAddScreen : Screen("task/add")
    data object TaskEditScreen : Screen("task/edit/{taskId}") {
        fun createRoute(taskId: String): String = "task/edit/$taskId"
    }
}
