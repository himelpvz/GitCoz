package com.hypex.gitcoz.presentation.screens.releases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypex.gitcoz.domain.model.GitHubRelease
import com.hypex.gitcoz.domain.repository.GitHubRepositoryContract
import com.hypex.gitcoz.presentation.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ReleasesTab { YOUR_RELEASES, LATEST_RELEASES }

data class ReleasesUiState(
    val selectedTab: ReleasesTab = ReleasesTab.LATEST_RELEASES,
    val yourReleasesState: UiState<List<GitHubRelease>> = UiState.Idle,
    val latestReleasesState: UiState<List<GitHubRelease>> = UiState.Idle
)

class ReleasesViewModel(
    private val repository: GitHubRepositoryContract
) : ViewModel() {

    private val _state = MutableStateFlow(ReleasesUiState())
    val state: StateFlow<ReleasesUiState> = _state.asStateFlow()

    private val popularRepos = listOf(
        "square" to "retrofit",
        "square" to "okhttp",
        "google" to "dagger",
        "google" to "gson",
        "JetBrains" to "kotlin",
        "androidx" to "compose-runtime",
        "coil-kt" to "coil",
        "insert-koin-io" to "koin",
        "airbnb" to "lottie-android",
        "facebook" to "fresco"
    )

    init {
        loadLatestReleases()
    }

    fun onTabSelected(tab: ReleasesTab) {
        _state.update { it.copy(selectedTab = tab) }
        if (tab == ReleasesTab.YOUR_RELEASES && _state.value.yourReleasesState is UiState.Idle) {
            loadYourReleases()
        }
    }

    private fun loadLatestReleases() {
        viewModelScope.launch {
            _state.update { it.copy(latestReleasesState = UiState.Loading) }
            repository.getTopReleases(popularRepos)
                .catch { e ->
                    _state.update {
                        it.copy(
                            latestReleasesState = UiState.Error(e.message ?: "Unknown error")
                        )
                    }
                }
                .collect { releases ->
                    _state.update { it.copy(latestReleasesState = UiState.Success(releases)) }
                }
        }
    }

    private fun loadYourReleases() {
        viewModelScope.launch {
            _state.update { it.copy(yourReleasesState = UiState.Loading) }
            try {
                val currentUser = repository.getAuthenticatedUser().first()
                val userRepos = repository.getUserRepos(currentUser.login).first()
                if (userRepos.isEmpty()) {
                    _state.update { it.copy(yourReleasesState = UiState.Success(emptyList())) }
                    return@launch
                }

                val allUserReleases = userRepos.map { repo ->
                    async {
                        runCatching {
                            repository.getRepoReleases(repo.ownerLogin, repo.name).first()
                        }.getOrDefault(emptyList())
                    }
                }.flatMap { it.await() }
                    .distinctBy { it.htmlUrl }
                    .sortedByDescending { it.publishedAt }

                _state.update {
                    it.copy(yourReleasesState = UiState.Success(allUserReleases))
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        yourReleasesState = UiState.Error(
                            e.message ?: "Unable to load your releases"
                        )
                    )
                }
            }
        }
    }
}
