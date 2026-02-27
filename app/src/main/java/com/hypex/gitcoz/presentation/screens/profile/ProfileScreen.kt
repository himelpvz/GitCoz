package com.hypex.gitcoz.presentation.screens.profile

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hypex.gitcoz.presentation.AppViewModelProvider
import androidx.compose.ui.res.painterResource
import com.hypex.gitcoz.R
import com.hypex.gitcoz.presentation.UiState
import com.hypex.gitcoz.domain.model.GitHubUser
import com.hypex.gitcoz.domain.model.GitHubRepository
import com.hypex.gitcoz.presentation.components.GitHubCard
import com.hypex.gitcoz.presentation.components.LoadingCardListPlaceholder
import com.hypex.gitcoz.presentation.components.ProfileCard
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import com.hypex.gitcoz.GitCozApp
import com.hypex.gitcoz.presentation.components.SaaSTopBar
import com.hypex.gitcoz.ui.theme.ElectricViolet
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun ProfileScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToRepo: (String, String) -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    var visibleRepoCount by remember { mutableStateOf(10) }

    val context = LocalContext.current
    val authManager = (context.applicationContext as GitCozApp).container.authManager
    val displayName = authManager.getDisplayName() ?: "Developer"
    val username = authManager.getUsername()

    LaunchedEffect(username) {
        if (!username.isNullOrBlank()) {
            viewModel.loadProfile(username)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // SaaS Top Bar
        SaaSTopBar(
            title = "My Profile",
            subtitle = "Hello, $displayName",
            actions = {
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notifications),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        // SCROLLABLE Content
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
        ) {
            item {
                AIUpdatesFeed()
            }

            when (val userState = state.userState) {
                is UiState.Loading -> item { LoadingCardListPlaceholder(count = 1) }
                is UiState.Success -> item { UserHeader(user = userState.data, onNavigateToDetail = onNavigateToDetail) }
                is UiState.Error -> item { Text("Error: ${userState.message}", color = MaterialTheme.colorScheme.error) }
                else -> {}
            }

            when (val reposState = state.reposState) {
                is UiState.Loading -> {
                    item {
                        Text(
                            "Public Repositories",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    item { LoadingCardListPlaceholder(count = 3) }
                }
                is UiState.Success -> {
                    item {
                        Text(
                            "Public Repositories", 
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(
                        items = reposState.data.take(visibleRepoCount),
                        key = { repo -> repo.id }
                    ) { repo ->
                        RepoItem(repo = repo) {
                            onNavigateToRepo(repo.ownerLogin, repo.name)
                        }
                    }
                    if (visibleRepoCount < reposState.data.size) {
                        item { LoadingCardListPlaceholder(count = 1) }
                    }
                }
                is UiState.Error -> item {
                    Text("Error: ${reposState.message}", color = MaterialTheme.colorScheme.error)
                }
                else -> {}
            }
        }
    }

    val repoItems = (state.reposState as? UiState.Success<List<GitHubRepository>>)?.data.orEmpty()
    LaunchedEffect(repoItems.size) {
        visibleRepoCount = minOf(10, repoItems.size)
    }
    LaunchedEffect(listState, repoItems.size, visibleRepoCount) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .map { it >= visibleRepoCount - 3 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                if (visibleRepoCount < repoItems.size) {
                    visibleRepoCount = minOf(visibleRepoCount + 10, repoItems.size)
                }
            }
    }
}

@Composable
fun AIUpdatesFeed() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ElectricViolet)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_ai_sparkle), 
                contentDescription = null, 
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "AI Update Feed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Try searching 'google' for trending repos!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun UserHeader(user: GitHubUser, onNavigateToDetail: (String) -> Unit) {
    ProfileCard(
        user = user,
        onViewProfile = { onNavigateToDetail(user.login) }
    )
}

@Composable
fun RepoItem(repo: GitHubRepository, onClick: () -> Unit) {
    GitHubCard(
        title = repo.name,
        subtitle = repo.ownerLogin,
        statusText = "Premium Repository",
        avatarUrl = repo.ownerAvatar,
        label1 = "Language",
        value1 = repo.language,
        label2 = "Stars",
        value2 = "⭐ ${repo.stars}",
        buttonText = "Checkout Code",
        isPremium = false,
        isRepo = true,
        onButtonClick = onClick
    )
}
