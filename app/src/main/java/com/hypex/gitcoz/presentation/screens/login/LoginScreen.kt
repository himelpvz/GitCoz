package com.hypex.gitcoz.presentation.screens.login

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hypex.gitcoz.R
import com.hypex.gitcoz.presentation.AppViewModelProvider
import com.hypex.gitcoz.presentation.UiState
import com.hypex.gitcoz.presentation.components.LoadingCardListPlaceholder
import com.hypex.gitcoz.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.authState) {
        if (state.authState is UiState.Success) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CardGradientStart, CardGradientEnd, Color(0xFF312E81))
                )
            )
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.8f))

            // App Icon
            Icon(
                painter = painterResource(id = R.drawable.ic_ai_sparkle),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = "GitCoz",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your Premium GitHub Explorer",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Feature highlights
            FeatureItem(text = "Explore trending repositories worldwide")
            Spacer(modifier = Modifier.height(12.dp))
            FeatureItem(text = "Track releases from your favorite projects")
            Spacer(modifier = Modifier.height(12.dp))
            FeatureItem(text = "Discover developer profiles with rich details")

            Spacer(modifier = Modifier.weight(1f))

            // Login Button
            when (state.authState) {
                is UiState.Loading -> {
                    LoadingCardListPlaceholder(count = 1)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Signing you in...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                is UiState.Error -> {
                    Text(
                        text = (state.authState as UiState.Error).message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFFB4AB),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    GitHubSignInButton {
                        val authUrl = viewModel.getAuthUrl()
                        val customTabsIntent = CustomTabsIntent.Builder()
                            .setShowTitle(true)
                            .build()
                        customTabsIntent.launchUrl(context, Uri.parse(authUrl))
                    }
                }
                else -> {
                    GitHubSignInButton {
                        val authUrl = viewModel.getAuthUrl()
                        val customTabsIntent = CustomTabsIntent.Builder()
                            .setShowTitle(true)
                            .build()
                        customTabsIntent.launchUrl(context, Uri.parse(authUrl))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "By signing in, you agree to GitHub's Terms of Service",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun GitHubSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF24292F)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_nav_profile),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF24292F)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Sign in with GitHub",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            color = Color.White.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ai_sparkle),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = CardStar
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}
