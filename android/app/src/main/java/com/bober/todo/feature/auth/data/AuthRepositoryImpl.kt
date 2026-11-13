package com.bober.todo.feature.auth.data

import com.bober.todo.core.network.SafeApiCall
import com.bober.todo.feature.auth.data.local.TokenStore
import com.bober.todo.feature.auth.data.remote.AuthApi
import com.bober.todo.feature.auth.data.remote.dto.AuthResponseDto
import com.bober.todo.feature.auth.data.remote.dto.LoginRequestDto
import com.bober.todo.feature.auth.data.remote.dto.RegisterRequestDto
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
    private val safeApiCall: SafeApiCall,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthResponseDto> {
        val result = safeApiCall.run(statusMessages = authStatusMessages) {
            authApi.login(LoginRequestDto(email = email, password = password))
        }
        result.getOrNull()?.let { saveSession(it) }
        return result
    }

    override suspend fun register(email: String, password: String): Result<AuthResponseDto> {
        val result = safeApiCall.run(statusMessages = authStatusMessages) {
            authApi.register(RegisterRequestDto(email = email, password = password))
        }
        result.getOrNull()?.let { saveSession(it) }
        return result
    }

    private suspend fun saveSession(dto: AuthResponseDto) {
        tokenStore.saveSession(
            accessToken = dto.accessToken,
            tokenType = dto.tokenType,
            email = dto.email,
        )
    }

    private companion object {
        val authStatusMessages = mapOf(
            401 to "Invalid email or password",
            409 to "Email already registered",
        )
    }
}
