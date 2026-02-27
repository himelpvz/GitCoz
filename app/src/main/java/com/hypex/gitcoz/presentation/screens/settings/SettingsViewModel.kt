package com.hypex.gitcoz.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypex.gitcoz.data.settings.AppSettings
import com.hypex.gitcoz.data.settings.GradientPalette
import com.hypex.gitcoz.data.settings.SettingsManager
import com.hypex.gitcoz.data.settings.ThemeMode
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsManager.settings

    fun updateThemeMode(mode: ThemeMode) {
        settingsManager.updateThemeMode(mode)
    }

    fun updateUseGradient(use: Boolean) {
        settingsManager.updateUseGradient(use)
    }

    fun updatePalette(palette: GradientPalette) {
        settingsManager.updatePalette(palette)
    }
}
