package com.hypex.gitcoz.presentation.screens.releases

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hypex.gitcoz.domain.model.GitHubRelease
import com.hypex.gitcoz.presentation.AppViewModelProvider
import com.hypex.gitcoz.presentation.UiState
import com.hypex.gitcoz.presentation.components.GitHubCard
import com.hypex.gitcoz.presentation.components.LoadingCardListPlaceholder
import com.hypex.gitcoz.presentation.components.SaaSTopBar
import com.hypex.gitcoz.ui.theme.CardButton
import com.hypex.gitcoz.ui.theme.CardButtonText
import com.hypex.gitcoz.ui.theme.ElectricViolet
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
fun ReleasesScreen(
    viewModel: ReleasesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var visibleCount by remember { mutableStateOf(10) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SaaSTopBar(title = "Releases")

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReleaseTabChip(
                    title = "Your Releases",
                    selected = state.selectedTab == ReleasesTab.YOUR_RELEASES,
                    onClick = { viewModel.onTabSelected(ReleasesTab.YOUR_RELEASES) },
                    modifier = Modifier.weight(1f)
                )
                ReleaseTabChip(
                    title = "Latest Releases",
                    selected = state.selectedTab == ReleasesTab.LATEST_RELEASES,
                    onClick = { viewModel.onTabSelected(ReleasesTab.LATEST_RELEASES) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val selectedState = when (state.selectedTab) {
                ReleasesTab.YOUR_RELEASES -> state.yourReleasesState
                ReleasesTab.LATEST_RELEASES -> state.latestReleasesState
            }

            when (selectedState) {
                UiState.Idle, UiState.Loading -> LoadingCardListPlaceholder(count = 4)
                is UiState.Success -> {
                    val releases = selectedState.data
                    if (releases.isEmpty()) {
                        Text(
                            text = if (state.selectedTab == ReleasesTab.YOUR_RELEASES) {
                                "No releases found in your repositories."
                            } else {
                                "No releases found."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            items(
                                items = releases.take(visibleCount),
                                key = { release -> release.htmlUrl }
                            ) { release ->
                                ReleaseItem(release = release) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(release.htmlUrl))
                                    context.startActivity(intent)
                                }
                            }
                            if (visibleCount < releases.size) {
                                item { LoadingCardListPlaceholder(count = 1) }
                            }
                        }
                    }
                }
                is UiState.Error -> Text(
                    "Error: ${selectedState.message}",
                    color = MaterialTheme.colorScheme.error
                )
                else -> {}
            }
        }
    }

    val currentItems = when (state.selectedTab) {
        ReleasesTab.YOUR_RELEASES -> (state.yourReleasesState as? UiState.Success)?.data.orEmpty()
        ReleasesTab.LATEST_RELEASES -> (state.latestReleasesState as? UiState.Success)?.data.orEmpty()
    }

    LaunchedEffect(state.selectedTab, currentItems.size) {
        visibleCount = minOf(10, currentItems.size)
    }

    LaunchedEffect(listState, state.selectedTab, currentItems.size, visibleCount) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .map { it >= visibleCount - 3 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                if (visibleCount < currentItems.size) {
                    visibleCount = minOf(visibleCount + 10, currentItems.size)
                }
            }
    }
}

@Composable
private fun ReleaseTabChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(title) },
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(12.dp),
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
            selected = selected
        )
    )
}

@Composable
fun ReleaseItem(release: GitHubRelease, onClick: () -> Unit) {
    GitHubCard(
        title = release.name ?: release.tagName,
        subtitle = release.repoName,
        statusText = "Verified Release",
        avatarUrl = null,
        label1 = "Stable Tag",
        value1 = release.tagName,
        label2 = "Global Rollout",
        value2 = release.publishedAt.take(10),
        buttonText = "Install Release",
        githubUrl = release.htmlUrl,
        isPremium = true,
        onButtonClick = onClick
    )
}
