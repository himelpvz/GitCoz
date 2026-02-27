package com.hypex.gitcoz.presentation.screens.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.hypex.gitcoz.R
import com.hypex.gitcoz.presentation.AppViewModelProvider
import com.hypex.gitcoz.domain.model.GitHubRepository
import com.hypex.gitcoz.domain.model.GitHubUser
import com.hypex.gitcoz.presentation.UiState
import com.hypex.gitcoz.presentation.components.LoadingCardListPlaceholder
import com.hypex.gitcoz.presentation.screens.trending.TrendingRepoItem
import com.hypex.gitcoz.presentation.components.SaaSTopBar
import com.hypex.gitcoz.ui.theme.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun SearchScreen(
    onNavigateToRepo: (String, String) -> Unit,
    onNavigateToProfile: (String) -> Unit = {},
    viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val query by viewModel.searchQuery.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val repoState by viewModel.repoState.collectAsState()
    val userState by viewModel.userState.collectAsState()
    val repoListState = rememberLazyListState()
    val userListState = rememberLazyListState()
    var visibleRepoCount by remember { mutableStateOf(10) }
    var visibleUserCount by remember { mutableStateOf(10) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SaaSTopBar(title = "Search")

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                placeholder = {
                    Text(
                        if (selectedTab == SearchTab.REPOSITORIES) "Search repositories..." else "Search users...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = ElectricViolet
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F5F9),
                    unfocusedContainerColor = Color(0xFFF1F5F9),
                    focusedBorderColor = ElectricViolet,
                    unfocusedBorderColor = ElectricViolet.copy(alpha = 0.3f),
                    cursorColor = ElectricViolet
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = ElectricViolet.copy(alpha = 0.15f),
                        spotColor = ElectricViolet.copy(alpha = 0.15f)
                    )
                    .border(
                        width = 1.dp,
                        color = ElectricViolet.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SearchTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onTabChange(tab) },
                        label = {
                            Text(
                                if (tab == SearchTab.REPOSITORIES) "Repositories" else "Users",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricViolet,
                            selectedLabelColor = Color.White,
                            containerColor = CardButton,
                            labelColor = CardButtonText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = ElectricViolet.copy(alpha = 0.2f),
                            selectedBorderColor = ElectricViolet,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Results
            when (selectedTab) {
                SearchTab.REPOSITORIES -> {
                    when (repoState) {
                        is UiState.Loading -> {
                            LoadingCardListPlaceholder(
                                count = 4,
                                modifier = Modifier.padding(bottom = 120.dp)
                            )
                        }
                        is UiState.Success -> {
                            val repos = (repoState as UiState.Success<List<GitHubRepository>>).data
                            if (repos.isEmpty()) {
                                Text("No repositories found.", modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)
                            } else {
                                LazyColumn(
                                    state = repoListState,
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(bottom = 120.dp)
                                ) {
                                    items(
                                        items = repos.take(visibleRepoCount),
                                        key = { repo -> repo.id }
                                    ) { repo ->
                                        TrendingRepoItem(repo = repo) {
                                            onNavigateToRepo(repo.ownerLogin, repo.name)
                                        }
                                    }
                                    if (visibleRepoCount < repos.size) {
                                        item { LoadingCardListPlaceholder(count = 1) }
                                    }
                                }
                            }
                        }
                        is UiState.Error -> Text("Error: ${(repoState as UiState.Error).message}", color = MaterialTheme.colorScheme.error)
                        else -> {}
                    }
                }
                SearchTab.USERS -> {
                    when (userState) {
                        is UiState.Loading -> {
                            LoadingCardListPlaceholder(
                                count = 4,
                                modifier = Modifier.padding(bottom = 120.dp)
                            )
                        }
                        is UiState.Success -> {
                            val users = (userState as UiState.Success<List<GitHubUser>>).data
                            if (users.isEmpty()) {
                                Text("No users found.", modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)
                            } else {
                                LazyColumn(
                                    state = userListState,
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(bottom = 120.dp)
                                ) {
                                    items(
                                        items = users.take(visibleUserCount),
                                        key = { user -> user.login }
                                    ) { user ->
                                        SearchUserCard(user = user) {
                                            onNavigateToProfile(user.login)
                                        }
                                    }
                                    if (visibleUserCount < users.size) {
                                        item { LoadingCardListPlaceholder(count = 1) }
                                    }
                                }
                            }
                        }
                        is UiState.Error -> Text("Error: ${(userState as UiState.Error).message}", color = MaterialTheme.colorScheme.error)
                        else -> {}
                    }
                }
            }
        }
    }

    val repoItems = (repoState as? UiState.Success<List<GitHubRepository>>)?.data.orEmpty()
    val userItems = (userState as? UiState.Success<List<GitHubUser>>)?.data.orEmpty()

    LaunchedEffect(repoItems.size, selectedTab, query) {
        if (selectedTab == SearchTab.REPOSITORIES) {
            visibleRepoCount = minOf(10, repoItems.size)
        }
    }
    LaunchedEffect(userItems.size, selectedTab, query) {
        if (selectedTab == SearchTab.USERS) {
            visibleUserCount = minOf(10, userItems.size)
        }
    }

    LaunchedEffect(repoListState, selectedTab, repoItems.size, visibleRepoCount) {
        if (selectedTab != SearchTab.REPOSITORIES) return@LaunchedEffect
        snapshotFlow { repoListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .map { it >= visibleRepoCount - 3 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                if (visibleRepoCount < repoItems.size) {
                    visibleRepoCount = minOf(visibleRepoCount + 10, repoItems.size)
                }
            }
    }

    LaunchedEffect(userListState, selectedTab, userItems.size, visibleUserCount) {
        if (selectedTab != SearchTab.USERS) return@LaunchedEffect
        snapshotFlow { userListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .map { it >= visibleUserCount - 3 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                if (visibleUserCount < userItems.size) {
                    visibleUserCount = minOf(visibleUserCount + 10, userItems.size)
                }
            }
    }
}

@Composable
fun SearchUserCard(user: GitHubUser, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBottomBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, CardGradientStart.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                color = CardGradientStart.copy(alpha = 0.1f),
                border = BorderStroke(2.dp, CardGradientStart.copy(alpha = 0.3f))
            ) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = user.login,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CardPrimaryText
                )
                Text(
                    text = "@${user.login}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CardSecondaryText
                )
                if (user.bio.isNotEmpty() && user.bio != "No bio available") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodySmall,
                        color = CardSecondaryText,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Stats Column
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = user.followers.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = CardGradientStart
                )
                Text(
                    text = "followers",
                    style = MaterialTheme.typography.labelSmall,
                    color = CardSecondaryText
                )
            }
        }
    }
}
