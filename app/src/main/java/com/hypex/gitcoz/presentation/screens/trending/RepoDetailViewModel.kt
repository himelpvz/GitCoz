package com.hypex.gitcoz.presentation.screens.trending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypex.gitcoz.domain.repository.GitHubRepositoryContract
import com.hypex.gitcoz.presentation.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RepoDetailUiState(
    val readmeState: UiState<String> = UiState.Idle
)

class RepoDetailViewModel(
    private val repository: GitHubRepositoryContract
) : ViewModel() {

    private val _state = MutableStateFlow(RepoDetailUiState())
    val state: StateFlow<RepoDetailUiState> = _state.asStateFlow()

    fun loadRepoDetail(owner: String, repo: String) {
        viewModelScope.launch {
            _state.update { it.copy(readmeState = UiState.Loading) }

            repository.getReadme(owner, repo)
                .onEach { readme ->
                    _state.update { it.copy(readmeState = UiState.Success(readme)) }
                }
                .catch { 
                    _state.update { it.copy(readmeState = UiState.Error("README not found")) }
                }
                .launchIn(viewModelScope)
        }
    }
}
