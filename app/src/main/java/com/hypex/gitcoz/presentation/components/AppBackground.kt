package com.hypex.gitcoz.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.hypex.gitcoz.GitCozApp
import com.hypex.gitcoz.data.settings.GradientPalette
import com.hypex.gitcoz.data.settings.ThemeMode
import com.hypex.gitcoz.ui.theme.*

@Composable
fun AppBackground(
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val settingsManager = (context.applicationContext as GitCozApp).container.settingsManager
    val settings by settingsManager.settings.collectAsState()

    val isDark = when (settings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colors = if (!settings.useGradient) {
        listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background)
    } else {
        when (settings.palette) {
            GradientPalette.AURORA -> if (isDark) {
                listOf(BgGradientDarkStart, BgGradientDarkMid, BgGradientDarkEnd)
            } else {
                listOf(BgGradientStart, BgGradientMid, BgGradientEnd)
            }
            GradientPalette.COSMIC -> if (isDark) {
                listOf(BgGradientDarkStart, BgGradientDarkMid, BgGradientDarkEnd)
            } else {
                listOf(BgGradientStart, BgGradientMid, BgGradientEnd)
            }
            GradientPalette.SUNSET -> listOf(SunsetStart, SunsetMid, SunsetEnd)
            GradientPalette.OCEAN -> listOf(OceanStart, OceanMid, OceanEnd)
            GradientPalette.FOREST -> listOf(ForestStart, ForestMid, ForestEnd)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colors))
    ) {
        content()
    }
}
