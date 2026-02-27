package com.hypex.gitcoz.presentation.screens.trending

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hypex.gitcoz.R
import com.hypex.gitcoz.domain.model.GitHubRepository
import com.hypex.gitcoz.presentation.AppViewModelProvider
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.hypex.gitcoz.presentation.UiState
import com.hypex.gitcoz.presentation.components.GitHubCard
import com.hypex.gitcoz.presentation.components.LoadingCardListPlaceholder
import com.hypex.gitcoz.presentation.components.SaaSTopBar
import com.hypex.gitcoz.ui.theme.ElectricViolet
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

enum class ExploreTab { TOP, TRENDING }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingScreen(
    onNavigateToRepo: (String, String) -> Unit,
    viewModel: TrendingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedTab by remember { mutableStateOf(ExploreTab.TOP) }
    val listState = rememberLazyListState()
    var visibleTopCount by remember { mutableStateOf(10) }
    var visibleTrendingCount by remember { mutableStateOf(10) }
    
    val topFilters = listOf("All", "JavaScript", "Python", "Java", "Go", "Rust", "C++", "TypeScript")
    val trendingFilters = listOf("All", "Android", "Kotlin", "Swift", "Python", "JavaScript", "Go", "Rust")

    LaunchedEffect(selectedTab) {
        if (selectedTab == ExploreTab.TRENDING) {
            viewModel.ensureTrendingLoaded()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SaaSTopBar(
            title = "Discover",
            actions = {
                if (selectedTab == ExploreTab.TRENDING) {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar), 
                            contentDescription = "Select Date",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = Color.Transparent,
                contentColor = ElectricViolet,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = ElectricViolet
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == ExploreTab.TOP,
                    onClick = { selectedTab = ExploreTab.TOP },
                    text = { Text("Top Repos", fontWeight = if (selectedTab == ExploreTab.TOP) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == ExploreTab.TRENDING,
                    onClick = { selectedTab = ExploreTab.TRENDING },
                    text = { Text("Trending", fontWeight = if (selectedTab == ExploreTab.TRENDING) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                ExploreTab.TOP -> {
                    FilterRow(
                        filters = topFilters,
                        selectedFilter = state.selectedTopFilter,
                        onFilterSelected = { viewModel.setTopFilter(it) }
                    )
                }
                ExploreTab.TRENDING -> {
                    FilterRow(
                        filters = trendingFilters,
                        selectedFilter = state.selectedTrendingFilter,
                        onFilterSelected = { viewModel.setTrendingFilter(it) }
                    )
                }
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { viewModel.loadTrending(it) }
                            showDatePicker = false
                        }) { 
                            Text("OK", color = ElectricViolet, fontWeight = FontWeight.Bold) 
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                when (selectedTab) {
                    ExploreTab.TOP -> {
                        when (val topState = state.topState) {
                            is UiState.Loading -> item { LoadingCardListPlaceholder(count = 4) }
                            is UiState.Success -> {
                                val repos = topState.data
                                val filteredRepos = if (state.selectedTopFilter == "All") repos else repos.filter { it.language.equals(state.selectedTopFilter, ignoreCase = true) }
                                val visibleItems = filteredRepos.take(visibleTopCount)
                                items(
                                    items = visibleItems,
                                    key = { repo -> repo.id }
                                ) { repo ->
                                    TopRepoItem(repo = repo) {
                                        onNavigateToRepo(repo.ownerLogin, repo.name)
                                    }
                                }
                                if (visibleTopCount < filteredRepos.size) {
                                    item { LoadingCardListPlaceholder(count = 1) }
                                }
                            }
                            is UiState.Error -> item { Text("Error: ${topState.message}", color = MaterialTheme.colorScheme.error) }
                            else -> {}
                        }
                    }
                    ExploreTab.TRENDING -> {
                        when (val trendingState = state.trendingState) {
                            is UiState.Loading -> item { LoadingCardListPlaceholder(count = 4) }
                            is UiState.Success -> {
                                val repos = trendingState.data
                                val filteredRepos = if (state.selectedTrendingFilter == "All") repos else repos.filter { it.language.equals(state.selectedTrendingFilter, ignoreCase = true) }
                                val visibleItems = filteredRepos.take(visibleTrendingCount)
                                items(
                                    items = visibleItems,
                                    key = { repo -> repo.id }
                                ) { repo ->
                                    TrendingRepoItem(repo = repo) {
                                        onNavigateToRepo(repo.ownerLogin, repo.name)
                                    }
                                }
                                if (visibleTrendingCount < filteredRepos.size) {
                                    item { LoadingCardListPlaceholder(count = 1) }
                                }
                            }
                            is UiState.Error -> item { Text("Error: ${trendingState.message}", color = MaterialTheme.colorScheme.error) }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    val topRepos = (state.topState as? UiState.Success)?.data.orEmpty()
    val topFiltered = if (state.selectedTopFilter == "All") topRepos else {
        topRepos.filter { it.language.equals(state.selectedTopFilter, ignoreCase = true) }
    }
    val trendingRepos = (state.trendingState as? UiState.Success)?.data.orEmpty()
    val trendingFiltered = if (state.selectedTrendingFilter == "All") trendingRepos else {
        trendingRepos.filter { it.language.equals(state.selectedTrendingFilter, ignoreCase = true) }
    }
    val currentTotal = if (selectedTab == ExploreTab.TOP) topFiltered.size else trendingFiltered.size
    val currentVisible = if (selectedTab == ExploreTab.TOP) visibleTopCount else visibleTrendingCount

    LaunchedEffect(selectedTab, state.selectedTopFilter, state.selectedTrendingFilter, currentTotal) {
        if (selectedTab == ExploreTab.TOP) {
            visibleTopCount = minOf(10, currentTotal)
        } else {
            visibleTrendingCount = minOf(10, currentTotal)
        }
    }

    LaunchedEffect(listState, selectedTab, currentTotal, currentVisible) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
            .map { lastVisible -> lastVisible >= currentVisible - 3 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                if (selectedTab == ExploreTab.TOP && visibleTopCount < currentTotal) {
                    visibleTopCount = minOf(visibleTopCount + 10, currentTotal)
                } else if (selectedTab == ExploreTab.TRENDING && visibleTrendingCount < currentTotal) {
                    visibleTrendingCount = minOf(visibleTrendingCount + 10, currentTotal)
                }
            }
    }
}

@Composable
fun FilterRow(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ElectricViolet,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFF1F5F9),
                    labelColor = Color(0xFF64748B)
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun TopRepoItem(repo: GitHubRepository, onClick: () -> Unit) {
    GitHubCard(
        title = repo.name,
        subtitle = repo.ownerLogin,
        statusText = "Top Rated",
        avatarUrl = repo.ownerAvatar,
        label1 = "Language",
        value1 = repo.language,
        label2 = "Stars",
        value2 = "⭐ ${repo.stars}",
        buttonText = "View Project",
        isPremium = true,
        isRepo = true,
        onButtonClick = onClick
    )
}

@Composable
fun TrendingRepoItem(repo: GitHubRepository, onClick: () -> Unit) {
    GitHubCard(
        title = repo.name,
        subtitle = repo.ownerLogin,
        statusText = "Trending Now",
        avatarUrl = repo.ownerAvatar,
        label1 = "Language",
        value1 = repo.language,
        label2 = "Stars",
        value2 = "⭐ ${repo.stars}",
        buttonText = "Explore",
        isPremium = false,
        isRepo = true,
        onButtonClick = onClick
    )
}
