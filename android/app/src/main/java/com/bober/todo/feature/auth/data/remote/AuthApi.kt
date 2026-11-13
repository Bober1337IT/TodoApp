package com.bober.todo.feature.auth.data.remote

import com.bober.todo.feature.auth.data.remote.dto.AuthResponseDto
import com.bober.todo.feature.auth.data.remote.dto.LoginRequestDto
import com.bober.todo.feature.auth.data.remote.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body body: LoginRequestDto,
    ): AuthResponseDto

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body body: RegisterRequestDto,
    ): AuthResponseDto
}
