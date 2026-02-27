package com.hypex.gitcoz.presentation

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hypex.gitcoz.GitCozApp
import com.hypex.gitcoz.presentation.screens.profile.ProfileDetailViewModel
import com.hypex.gitcoz.presentation.screens.profile.ProfileViewModel
import com.hypex.gitcoz.presentation.screens.releases.ReleasesViewModel
import com.hypex.gitcoz.presentation.screens.search.SearchViewModel
import com.hypex.gitcoz.presentation.screens.settings.SettingsViewModel
import com.hypex.gitcoz.presentation.screens.settings.FeedbackViewModel
import com.hypex.gitcoz.presentation.screens.trending.RepoDetailViewModel
import com.hypex.gitcoz.presentation.screens.trending.TrendingViewModel
import com.hypex.gitcoz.presentation.screens.login.LoginViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            LoginViewModel(
                authManager = gitCozApplication().container.authManager,
                repository = gitCozApplication().container.gitHubRepository
            )
        }
        initializer {
            ProfileViewModel(
                repository = gitCozApplication().container.gitHubRepository
            )
        }
        initializer {
            ProfileDetailViewModel(
                repository = gitCozApplication().container.gitHubRepository
            )
        }
        initializer {
            TrendingViewModel(
                repository = gitCozApplication().container.gitHubRepository
            )
        }
        initializer {
            RepoDetailViewModel(
                repository = gitCozApplication().container.gitHubRepository
            )
        }
        initializer {
            SearchViewModel(
                repository = gitCozApplication().container.gitHubRepository
            )
        }
        initializer {
            ReleasesViewModel(
                repository = gitCozApplication().container.gitHubRepository
            )
        }
        initializer {
            SettingsViewModel(
                settingsManager = gitCozApplication().container.settingsManager
            )
        }
        initializer {
            FeedbackViewModel(
                repository = gitCozApplication().container.gitHubRepository,
                telegramFeedbackSender = gitCozApplication().container.telegramFeedbackSender
            )
        }
    }
}

fun CreationExtras.gitCozApplication(): GitCozApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GitCozApp)
