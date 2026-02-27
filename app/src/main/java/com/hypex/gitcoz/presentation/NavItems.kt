package com.hypex.gitcoz.presentation

import androidx.annotation.DrawableRes
import com.hypex.gitcoz.R

sealed class Screen(val route: String, val title: String, @DrawableRes val icon: Int) {
    object Login : Screen("login", "Login", R.drawable.ic_nav_profile)
    object Profile : Screen("profile", "Profile", R.drawable.ic_nav_profile)
    object Trending : Screen("trending", "Trending", R.drawable.ic_nav_trending)
    object Search : Screen("search", "Search", R.drawable.ic_nav_search)
    object Releases : Screen("releases", "Releases", R.drawable.ic_nav_releases)
    object Settings : Screen("settings", "Settings", R.drawable.ic_nav_settings)
    object About : Screen("about_gitcoz", "About GitCoz", R.drawable.ic_ai_sparkle)
    object Feedback : Screen("feedback_gitcoz", "Feedback", R.drawable.ic_ai_sparkle)
    object ProfileDetail : Screen("profile_detail/{username}", "Profile Detail", R.drawable.ic_nav_profile)
    object RepoDetail : Screen("repo_detail/{owner}/{repo}", "Repo Detail", R.drawable.ic_nav_trending)
}

val navItems = listOf(
    Screen.Profile,
    Screen.Trending,
    Screen.Search,
    Screen.Releases,
    Screen.Settings
)
