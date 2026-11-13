package com.bober.todo.navigation

sealed class Screen(val route: String) {

    data object SplashScreen : Screen("splash")
    data object LoginScreen : Screen("login")
    data object RegisterScreen : Screen("register")
    data object TaskScreen : Screen("task")
}
