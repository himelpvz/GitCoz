package com.hypex.gitcoz

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hypex.gitcoz.data.settings.ThemeMode
import com.hypex.gitcoz.presentation.AppViewModelProvider
import com.hypex.gitcoz.presentation.Screen
import com.hypex.gitcoz.presentation.navItems
import com.hypex.gitcoz.presentation.components.AnimatedBottomBar
import com.hypex.gitcoz.presentation.components.AppBackground
import com.hypex.gitcoz.presentation.screens.login.LoginScreen
import com.hypex.gitcoz.presentation.screens.login.LoginViewModel
import com.hypex.gitcoz.presentation.screens.profile.ProfileDetailScreen
import com.hypex.gitcoz.presentation.screens.profile.ProfileScreen
import com.hypex.gitcoz.presentation.screens.releases.ReleasesScreen
import com.hypex.gitcoz.presentation.screens.search.SearchScreen
import com.hypex.gitcoz.presentation.screens.settings.AboutScreen
import com.hypex.gitcoz.presentation.screens.settings.FeedbackScreen
import com.hypex.gitcoz.presentation.screens.settings.SettingsScreen
import com.hypex.gitcoz.presentation.screens.trending.RepoDetailScreen
import com.hypex.gitcoz.presentation.screens.trending.TrendingScreen
import com.hypex.gitcoz.ui.theme.GitCozTheme

class MainActivity : ComponentActivity() {

    // Hold the OAuth code from the callback intent
    private var pendingAuthCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if launched via OAuth callback
        pendingAuthCode = extractAuthCode(intent)

        setContent {
            val app = applicationContext as GitCozApp
            val settingsManager = app.container.settingsManager
            val authManager = app.container.authManager
            val settings by settingsManager.settings.collectAsState()
            val isLoggedIn by authManager.isLoggedIn.collectAsState()

            val isDarkTheme = when (settings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val startDestination = if (isLoggedIn) Screen.Profile.route else Screen.Login.route

            GitCozTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Handle pending auth code
                val loginViewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)

                LaunchedEffect(pendingAuthCode) {
                    pendingAuthCode?.let { code ->
                        loginViewModel.handleAuthCode(code)
                        pendingAuthCode = null
                    }
                }

                AppBackground {
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable(Screen.Login.route) {
                                LoginScreen(
                                    onLoginSuccess = {
                                        navController.navigate(Screen.Profile.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    },
                                    viewModel = loginViewModel
                                )
                            }
                            composable(Screen.Profile.route) { 
                                ProfileScreen(
                                    onNavigateToDetail = { username ->
                                        navController.navigate("profile_detail/$username")
                                    },
                                    onNavigateToRepo = { owner, repo ->
                                        navController.navigate("repo_detail/$owner/$repo")
                                    }
                                ) 
                            }
                            composable(Screen.Trending.route) { 
                                TrendingScreen(
                                    onNavigateToRepo = { owner, repo ->
                                        navController.navigate("repo_detail/$owner/$repo")
                                    }
                                )
                            }
                            composable(Screen.Search.route) { 
                                SearchScreen(
                                    onNavigateToRepo = { owner, repo ->
                                        navController.navigate("repo_detail/$owner/$repo")
                                    },
                                    onNavigateToProfile = { username ->
                                        navController.navigate("profile_detail/$username")
                                    }
                                )
                            }
                            composable(Screen.Releases.route) { 
                                ReleasesScreen()
                            }
                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                    onNavigateToAbout = {
                                        navController.navigate(Screen.About.route)
                                    },
                                    onNavigateToFeedback = {
                                        navController.navigate(Screen.Feedback.route)
                                    },
                                    onLogout = {
                                        loginViewModel.logout()
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable(Screen.About.route) {
                                AboutScreen(onBack = { navController.popBackStack() })
                            }
                            composable(Screen.Feedback.route) {
                                FeedbackScreen(onBack = { navController.popBackStack() })
                            }
                            composable(
                                route = Screen.ProfileDetail.route,
                                arguments = listOf(navArgument("username") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val username = backStackEntry.arguments?.getString("username") ?: ""
                                ProfileDetailScreen(
                                    username = username,
                                    onBack = { navController.popBackStack() },
                                    onNavigateToRepo = { owner, repo ->
                                        navController.navigate("repo_detail/$owner/$repo")
                                    }
                                )
                            }
                            composable(
                                route = Screen.RepoDetail.route,
                                arguments = listOf(
                                    navArgument("owner") { type = NavType.StringType },
                                    navArgument("repo") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val owner = backStackEntry.arguments?.getString("owner") ?: ""
                                val repo = backStackEntry.arguments?.getString("repo") ?: ""
                                RepoDetailScreen(owner = owner, repo = repo, onBack = { navController.popBackStack() })
                            }
                        }

                        // Floating Animated Bottom Bar Overlay (hide on login screen)
                        val showBottomBar = navItems.any { it.route == currentRoute }
                        AnimatedVisibility(
                            visible = showBottomBar,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            AnimatedBottomBar(
                                screens = navItems,
                                currentRoute = currentRoute,
                                onNavigate = { screen ->
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val code = extractAuthCode(intent)
        if (code != null) {
            pendingAuthCode = code
            // Re-trigger composition
            setIntent(intent)
            recreate()
        }
    }

    private fun extractAuthCode(intent: Intent?): String? {
        val uri = intent?.data ?: return null
        if (uri.scheme == "gitcoz" && uri.host == "callback") {
            return uri.getQueryParameter("code")
        }
        return null
    }
}
