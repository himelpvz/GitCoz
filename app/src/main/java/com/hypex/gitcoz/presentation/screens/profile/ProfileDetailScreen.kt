package com.hypex.gitcoz.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hypex.gitcoz.R
import com.hypex.gitcoz.presentation.AppViewModelProvider
import com.hypex.gitcoz.presentation.UiState
import com.hypex.gitcoz.domain.model.GitHubRepository
import dev.jeziellago.compose.markdowntext.MarkdownText
import com.hypex.gitcoz.presentation.components.AppBackground
import com.hypex.gitcoz.presentation.components.ProfileCard
import com.hypex.gitcoz.presentation.components.GitHubCard
import com.hypex.gitcoz.presentation.components.LoadingCardListPlaceholder
import com.hypex.gitcoz.presentation.components.SaaSTopBar
import com.hypex.gitcoz.ui.theme.ElectricViolet
import com.hypex.gitcoz.ui.theme.CardBottomBg

@Composable
fun ProfileDetailScreen(
    username: String,
    onBack: () -> Unit,
    onNavigateToRepo: (String, String) -> Unit = { _, _ -> },
    viewModel: ProfileDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(username) {
        viewModel.loadProfileDetail(username)
    }

    AppBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            SaaSTopBar(
                title = username,
                subtitle = "User Profile",
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Back", modifier = Modifier.size(24.dp), tint = Color.White)
                    }
                }
            )

            when (val userState = state.userState) {
                is UiState.Loading -> Box(Modifier.fillMaxSize().padding(16.dp)) {
                    LoadingCardListPlaceholder(count = 2)
                }
                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Profile Card
                        item {
                            ProfileCard(user = userState.data)
                        }

                        // About / README Section
                        item {
                            Text(
                                "About",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBottomBg)
                            ) {
                                Box(modifier = Modifier.padding(16.dp)) {
                                    when (val readmeState = state.readmeState) {
                                        is UiState.Success -> MarkdownText(markdown = readmeState.data)
                                        is UiState.Loading -> LoadingCardListPlaceholder(count = 1)
                                        else -> Text(userState.data.bio, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }

                        // Repositories Section
                        when (val reposState = state.reposState) {
                            is UiState.Success -> {
                                val repos = reposState.data
                                if (repos.isNotEmpty()) {
                                    item {
                                        Text(
                                            "Repositories (${repos.size})",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                    items(repos) { repo ->
                                        GitHubCard(
                                            title = repo.name,
                                            subtitle = repo.ownerLogin,
                                            statusText = repo.language.takeIf { it != "Unknown" },
                                            avatarUrl = repo.ownerAvatar,
                                            label1 = "Language",
                                            value1 = repo.language,
                                            label2 = "Stars",
                                            value2 = "${repo.stars}",
                                            buttonText = "View Repository",
                                            githubUrl = repo.htmlUrl,
                                            isPremium = false,
                                            isRepo = true,
                                            onButtonClick = { onNavigateToRepo(repo.ownerLogin, repo.name) }
                                        )
                                    }
                                }
                            }
                            is UiState.Loading -> {
                                item {
                                    LoadingCardListPlaceholder(count = 3)
                                }
                            }
                            else -> {}
                        }

                        // Bottom spacing for nav bar
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
                is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(userState.message) }
                else -> {}
            }
        }
    }
}
