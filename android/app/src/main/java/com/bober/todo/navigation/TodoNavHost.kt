package com.bober.todo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.bober.todo.navigation.graph.loginNavGraph
import com.bober.todo.navigation.graph.registerNavGraph
import com.bober.todo.navigation.graph.splashNavGraph
import com.bober.todo.navigation.graph.taskNavGraph

@Composable
fun TodoNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route,
        modifier = modifier,
    ) {
        splashNavGraph(navController)
        loginNavGraph(navController)
        registerNavGraph(navController)
        taskNavGraph(navController)
    }
}
