package com.hypex.gitcoz.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypex.gitcoz.data.auth.AuthManager
import com.hypex.gitcoz.domain.repository.GitHubRepositoryContract
import com.hypex.gitcoz.presentation.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LoginUiState(
    val authState: UiState<Boolean> = UiState.Idle
)

class LoginViewModel(
    private val authManager: AuthManager,
    private val repository: GitHubRepositoryContract
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = authManager.isLoggedIn

    fun getAuthUrl(): String = authManager.getAuthUrl()

    fun handleAuthCode(code: String) {
        viewModelScope.launch {
            _state.update { it.copy(authState = UiState.Loading) }

            val result = authManager.exchangeCodeForToken(code)
            result.fold(
                onSuccess = { token ->
                    // Fetch authenticated user's profile info
                    repository.getUserProfile("")
                    // The token is already saved; now fetch the user info
                    fetchAuthenticatedUser()
                },
                onFailure = { error ->
                    _state.update { it.copy(authState = UiState.Error(error.message ?: "Login failed")) }
                }
            )
        }
    }

    private fun fetchAuthenticatedUser() {
        viewModelScope.launch {
            try {
                // GitHub API /user endpoint returns the authenticated user when token is set
                // We use a special call - the interceptor adds the Bearer token
                repository.getAuthenticatedUser()
                    .onEach { user ->
                        authManager.saveUserInfo(user.login, user.avatarUrl, user.name)
                        _state.update { it.copy(authState = UiState.Success(true)) }
                    }
                    .catch { e ->
                        // Token works but user fetch failed - still logged in
                        _state.update { it.copy(authState = UiState.Success(true)) }
                    }
                    .collect()
            } catch (e: Exception) {
                _state.update { it.copy(authState = UiState.Success(true)) }
            }
        }
    }

    fun logout() {
        authManager.logout()
        _state.update { it.copy(authState = UiState.Idle) }
    }
}
