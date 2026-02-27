package com.hypex.gitcoz.presentation.screens.trending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypex.gitcoz.domain.model.GitHubRepository
import com.hypex.gitcoz.domain.repository.GitHubRepositoryContract
import com.hypex.gitcoz.presentation.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class TrendingUiState(
    val trendingState: UiState<List<GitHubRepository>> = UiState.Idle,
    val topState: UiState<List<GitHubRepository>> = UiState.Idle,
    val selectedTrendingFilter: String = "All",
    val selectedTopFilter: String = "All"
)

class TrendingViewModel(
    private val repository: GitHubRepositoryContract
) : ViewModel() {

    private val _state = MutableStateFlow(TrendingUiState())
    val state: StateFlow<TrendingUiState> = _state.asStateFlow()

    init {
        loadTopRepos()
    }

    fun ensureTrendingLoaded() {
        if (_state.value.trendingState is UiState.Idle) {
            loadTrending()
        }
    }

    fun loadTrending(timestamp: Long? = null) {
        val calendar = Calendar.getInstance()
        if (timestamp != null) {
            calendar.timeInMillis = timestamp
        } else {
            calendar.add(Calendar.MONTH, -3)
        }
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        viewModelScope.launch {
            _state.update { it.copy(trendingState = UiState.Loading) }
            repository.getTrendingRepos(dateString)
                .catch { e -> _state.update { it.copy(trendingState = UiState.Error(e.message ?: "Unknown error")) } }
                .collect { repos -> _state.update { it.copy(trendingState = UiState.Success(repos)) } }
        }
    }

    fun loadTopRepos(language: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(topState = UiState.Loading) }
            repository.getTopRepos(language)
                .catch { e -> _state.update { it.copy(topState = UiState.Error(e.message ?: "Unknown error")) } }
                .collect { repos -> _state.update { it.copy(topState = UiState.Success(repos)) } }
        }
    }

    fun setTrendingFilter(filter: String) {
        _state.update { it.copy(selectedTrendingFilter = filter) }
    }

    fun setTopFilter(filter: String) {
        _state.update { it.copy(selectedTopFilter = filter) }
        loadTopRepos(if (filter == "All") null else filter)
    }
}
