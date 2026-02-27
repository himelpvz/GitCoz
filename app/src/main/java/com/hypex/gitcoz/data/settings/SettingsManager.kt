package com.hypex.gitcoz.data.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class GradientPalette { AURORA, COSMIC, SUNSET, OCEAN, FOREST }

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useGradient: Boolean = true,
    val palette: GradientPalette = GradientPalette.AURORA
)

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gitcoz_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        return AppSettings(
            themeMode = ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name),
            useGradient = prefs.getBoolean("use_gradient", true),
            palette = GradientPalette.valueOf(prefs.getString("palette", GradientPalette.AURORA.name) ?: GradientPalette.AURORA.name)
        )
    }

    fun updateThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _settings.update { it.copy(themeMode = mode) }
    }

    fun updateUseGradient(use: Boolean) {
        prefs.edit().putBoolean("use_gradient", use).apply()
        _settings.update { it.copy(useGradient = use) }
    }

    fun updatePalette(palette: GradientPalette) {
        prefs.edit().putString("palette", palette.name).apply()
        _settings.update { it.copy(palette = palette) }
    }
}
