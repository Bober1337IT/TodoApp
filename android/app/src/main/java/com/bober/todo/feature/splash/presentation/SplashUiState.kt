package com.bober.todo.feature.splash.presentation

enum class SplashNext {
    None,
    Login,
    Tasks,
}

data class SplashUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val next: SplashNext = SplashNext.None,
)
