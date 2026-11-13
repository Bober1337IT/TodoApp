package com.bober.todo.core.network

import com.bober.todo.core.network.dto.ErrorDto
import com.google.gson.Gson
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.HttpException

@Singleton
class SafeApiCall @Inject constructor(
    private val gson: Gson,
) {

    suspend fun <T> run(
        statusMessages: Map<Int, String> = emptyMap(),
        block: suspend () -> T,
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (e: HttpException) {
            Result.failure(Exception(httpErrorMessage(e, statusMessages)))
        } catch (e: IOException) {
            Result.failure(Exception("Cannot reach server. Check your connection."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Something went wrong"))
        }
    }

    private fun httpErrorMessage(
        exception: HttpException,
        statusMessages: Map<Int, String>,
    ): String {
        val body = exception.response()?.errorBody()?.string()
        if (!body.isNullOrBlank()) {
            val fromApi = runCatching {
                gson.fromJson(body, ErrorDto::class.java).error
            }.getOrNull()
            if (!fromApi.isNullOrBlank()) {
                return fromApi
            }
        }

        statusMessages[exception.code()]?.let { return it }

        return when (exception.code()) {
            in 500..599 -> "Server error. Try again later."
            else -> "Request failed (${exception.code()})"
        }
    }
}
