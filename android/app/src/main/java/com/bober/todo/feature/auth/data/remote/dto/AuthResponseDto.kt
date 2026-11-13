package com.bober.todo.feature.auth.data.remote.dto

data class AuthResponseDto(
    val accessToken: String,
    val tokenType: String,
    val email: String,
)
