package com.hypex.gitcoz.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypex.gitcoz.domain.model.GitHubRepository
import com.hypex.gitcoz.domain.model.GitHubUser
import com.hypex.gitcoz.domain.repository.GitHubRepositoryContract
import com.hypex.gitcoz.presentation.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userState: UiState<GitHubUser> = UiState.Idle,
    val reposState: UiState<List<GitHubRepository>> = UiState.Idle
)

class ProfileViewModel(
    private val repository: GitHubRepositoryContract
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun loadProfile(username: String) {
        if (username.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(userState = UiState.Loading, reposState = UiState.Loading) }

            repository.getUserProfile(username)
                .onEach { user ->
                    _state.update { it.copy(userState = UiState.Success(user)) }
                }
                .catch { e ->
                    _state.update { it.copy(userState = UiState.Error(e.message ?: "Unknown error")) }
                }
                .launchIn(viewModelScope)

            repository.getUserRepos(username)
                .onEach { repos ->
                    _state.update { it.copy(reposState = UiState.Success(repos)) }
                }
                .catch { e ->
                    _state.update { it.copy(reposState = UiState.Error(e.message ?: "Unknown error")) }
                }
                .launchIn(viewModelScope)
        }
    }

    fun loadAuthenticatedProfile() {
        viewModelScope.launch {
            _state.update { it.copy(userState = UiState.Loading, reposState = UiState.Loading) }

            try {
                val user = repository.getAuthenticatedUser().first()
                _state.update { it.copy(userState = UiState.Success(user)) }

                repository.getUserRepos(user.login)
                    .onEach { repos ->
                        _state.update { it.copy(reposState = UiState.Success(repos)) }
                    }
                    .catch { e ->
                        _state.update { it.copy(reposState = UiState.Error(e.message ?: "Unknown error")) }
                    }
                    .launchIn(viewModelScope)
            } catch (e: Exception) {
                val message = e.message ?: "Unknown error"
                _state.update { it.copy(userState = UiState.Error(message), reposState = UiState.Error(message)) }
            }
        }
    }
}
