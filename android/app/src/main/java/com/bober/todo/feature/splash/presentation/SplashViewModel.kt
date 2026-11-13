package com.bober.todo.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bober.todo.feature.splash.data.SessionCheckResult
import com.bober.todo.feature.splash.data.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    fun onRetry() {
        checkSession()
    }

    private fun checkSession() {
        _uiState.update {
            it.copy(isLoading = true, errorMessage = null, next = SplashNext.None)
        }
        viewModelScope.launch {
            when (val result = sessionRepository.checkSession()) {
                SessionCheckResult.Authenticated -> {
                    _uiState.update {
                        it.copy(isLoading = false, next = SplashNext.Tasks)
                    }
                }
                SessionCheckResult.Unauthenticated -> {
                    _uiState.update {
                        it.copy(isLoading = false, next = SplashNext.Login)
                    }
                }
                is SessionCheckResult.NetworkError -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            next = SplashNext.None,
                        )
                    }
                }
            }
        }
    }
}
