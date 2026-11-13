package com.bober.todo.feature.auth.data

import com.bober.todo.feature.auth.data.remote.dto.AuthResponseDto

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<AuthResponseDto>

    suspend fun register(email: String, password: String): Result<AuthResponseDto>
}
