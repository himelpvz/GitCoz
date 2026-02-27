package com.hypex.gitcoz.presentation.screens.trending

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import dev.jeziellago.compose.markdowntext.MarkdownText
import com.hypex.gitcoz.presentation.components.AppBackground
import com.hypex.gitcoz.presentation.components.LoadingCardListPlaceholder
import com.hypex.gitcoz.presentation.components.SaaSTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoDetailScreen(
    owner: String,
    repo: String,
    onBack: () -> Unit,
    viewModel: RepoDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(owner, repo) {
        viewModel.loadRepoDetail(owner, repo)
    }

    AppBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            SaaSTopBar(
                title = repo,
                subtitle = owner,
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Back", modifier = Modifier.size(24.dp), tint = Color.White)
                    }
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text("README.md", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            when (val readmeState = state.readmeState) {
                                is UiState.Loading -> LoadingCardListPlaceholder(count = 1)
                                is UiState.Success -> MarkdownText(markdown = readmeState.data)
                                is UiState.Error -> Text("README not available.")
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}
