package com.bober.todo.feature.splash.data

import com.bober.todo.feature.auth.data.local.TokenStore
import com.bober.todo.feature.task.data.remote.TasksApi
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.HttpException

sealed interface SessionCheckResult {
    data object Authenticated : SessionCheckResult
    data object Unauthenticated : SessionCheckResult
    data class NetworkError(val message: String) : SessionCheckResult
}

@Singleton
class SessionRepository @Inject constructor(
    private val tokenStore: TokenStore,
    private val tasksApi: TasksApi,
) {

    suspend fun checkSession(): SessionCheckResult {
        tokenStore.hydrate()

        if (tokenStore.getAccessToken().isNullOrBlank()) {
            return SessionCheckResult.Unauthenticated
        }

        return try {
            tasksApi.listTasks()
            SessionCheckResult.Authenticated
        } catch (e: HttpException) {
            if (e.code() == 401) {
                tokenStore.clear()
                SessionCheckResult.Unauthenticated
            } else {
                SessionCheckResult.NetworkError("Server error (${e.code()}). Try again.")
            }
        } catch (e: IOException) {
            SessionCheckResult.NetworkError("Cannot reach server. Check your connection.")
        } catch (e: Exception) {
            SessionCheckResult.NetworkError(e.message ?: "Something went wrong")
        }
    }
}
