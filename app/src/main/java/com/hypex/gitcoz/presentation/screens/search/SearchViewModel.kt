package com.hypex.gitcoz.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypex.gitcoz.domain.model.GitHubRepository
import com.hypex.gitcoz.domain.model.GitHubUser
import com.hypex.gitcoz.domain.repository.GitHubRepositoryContract
import com.hypex.gitcoz.presentation.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SearchTab { REPOSITORIES, USERS }

class SearchViewModel(
    private val repository: GitHubRepositoryContract
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTab = MutableStateFlow(SearchTab.REPOSITORIES)
    val selectedTab = _selectedTab.asStateFlow()

    private val _repoState = MutableStateFlow<UiState<List<GitHubRepository>>>(UiState.Idle)
    val repoState: StateFlow<UiState<List<GitHubRepository>>> = _repoState.asStateFlow()

    private val _userState = MutableStateFlow<UiState<List<GitHubUser>>>(UiState.Idle)
    val userState: StateFlow<UiState<List<GitHubUser>>> = _userState.asStateFlow()
    private var searchJob: Job? = null

    init {
        @OptIn(FlowPreview::class)
        _searchQuery
            .debounce(500)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isBlank()) {
                    searchJob?.cancel()
                    _repoState.value = UiState.Idle
                    _userState.value = UiState.Idle
                    return@onEach
                }
                performSearch(query)
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTabChange(tab: SearchTab) {
        _selectedTab.value = tab
        val currentQuery = _searchQuery.value
        if (currentQuery.isNotBlank()) {
            performSearch(currentQuery)
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            when (_selectedTab.value) {
                SearchTab.REPOSITORIES -> {
                    _repoState.value = UiState.Loading
                    repository.searchRepos(query)
                        .catch { e -> _repoState.value = UiState.Error(e.message ?: "Unknown error") }
                        .collect { repos -> _repoState.value = UiState.Success(repos) }
                }
                SearchTab.USERS -> {
                    _userState.value = UiState.Loading
                    repository.searchUsers(query)
                        .catch { e -> _userState.value = UiState.Error(e.message ?: "Unknown error") }
                        .collect { users -> _userState.value = UiState.Success(users) }
                }
            }
        }
    }
}
