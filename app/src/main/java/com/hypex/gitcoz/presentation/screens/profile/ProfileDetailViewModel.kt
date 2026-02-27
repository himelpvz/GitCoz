package com.hypex.gitcoz.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypex.gitcoz.domain.model.GitHubRepository
import com.hypex.gitcoz.domain.model.GitHubUser
import com.hypex.gitcoz.domain.repository.GitHubRepositoryContract
import com.hypex.gitcoz.presentation.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileDetailUiState(
    val userState: UiState<GitHubUser> = UiState.Idle,
    val readmeState: UiState<String> = UiState.Idle,
    val reposState: UiState<List<GitHubRepository>> = UiState.Idle
)

class ProfileDetailViewModel(
    private val repository: GitHubRepositoryContract
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileDetailUiState())
    val state: StateFlow<ProfileDetailUiState> = _state.asStateFlow()

    fun loadProfileDetail(username: String) {
        viewModelScope.launch {
            _state.update { it.copy(userState = UiState.Loading, readmeState = UiState.Loading, reposState = UiState.Loading) }

            repository.getUserProfile(username)
                .onEach { user ->
                    _state.update { it.copy(userState = UiState.Success(user)) }
                }
                .catch { e ->
                    _state.update { it.copy(userState = UiState.Error(e.message ?: "Unknown error")) }
                }
                .launchIn(viewModelScope)

            repository.getUserReadme(username)
                .onEach { readme ->
                    _state.update { it.copy(readmeState = UiState.Success(readme)) }
                }
                .catch { 
                    _state.update { it.copy(readmeState = UiState.Error("No README found")) }
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
}
