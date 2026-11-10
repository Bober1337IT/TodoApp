package com.bober.todo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.bober.todo.navigation.graph.loginNavGraph
import com.bober.todo.navigation.graph.registerNavGraph

@Composable
fun TodoNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route,
        modifier = modifier,
    ) {
        loginNavGraph(navController)
        registerNavGraph(navController)
    }
}
