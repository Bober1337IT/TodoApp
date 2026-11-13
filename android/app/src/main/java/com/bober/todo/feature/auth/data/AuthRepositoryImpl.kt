package com.bober.todo.feature.auth.data

import com.bober.todo.feature.auth.data.local.TokenStore
import com.bober.todo.feature.auth.data.remote.AuthApi
import com.bober.todo.feature.auth.data.remote.dto.AuthResponseDto
import com.bober.todo.feature.auth.data.remote.dto.ErrorDto
import com.bober.todo.feature.auth.data.remote.dto.LoginRequestDto
import com.bober.todo.feature.auth.data.remote.dto.RegisterRequestDto
import com.google.gson.Gson
import java.io.IOException
import javax.inject.Inject
import retrofit2.HttpException

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val gson: Gson,
    private val tokenStore: TokenStore,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthResponseDto> {
        val result = runAuthCall {
            authApi.login(LoginRequestDto(email = email, password = password))
        }
        result.getOrNull()?.let { saveSession(it) }
        return result
    }

    override suspend fun register(email: String, password: String): Result<AuthResponseDto> {
        val result = runAuthCall {
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

    private suspend fun runAuthCall(
        block: suspend () -> AuthResponseDto,
    ): Result<AuthResponseDto> {
        return try {
            Result.success(block())
        } catch (e: HttpException) {
            Result.failure(Exception(httpErrorMessage(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Cannot reach server. Check your connection."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Something went wrong"))
        }
    }

    private fun httpErrorMessage(exception: HttpException): String {
        val body = exception.response()?.errorBody()?.string()
        if (!body.isNullOrBlank()) {
            val fromApi = runCatching {
                gson.fromJson(body, ErrorDto::class.java).error
            }.getOrNull()
            if (!fromApi.isNullOrBlank()) {
                return fromApi
            }
        }

        return when (exception.code()) {
            401 -> "Invalid email or password"
            409 -> "Email already registered"
            in 500..599 -> "Server error. Try again later."
            else -> "Request failed (${exception.code()})"
        }
    }
}
