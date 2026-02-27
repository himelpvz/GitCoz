package com.hypex.gitcoz.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hypex.gitcoz.R
import com.hypex.gitcoz.data.settings.GradientPalette
import com.hypex.gitcoz.data.settings.ThemeMode
import com.hypex.gitcoz.presentation.AppViewModelProvider
import com.hypex.gitcoz.presentation.components.SaaSTopBar
import com.hypex.gitcoz.ui.theme.*

@Composable
fun SettingsScreen(
    onNavigateToAbout: () -> Unit = {},
    onNavigateToFeedback: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SaaSTopBar(
            title = "Settings",
            subtitle = "Customize your experience"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Theme Mode Section
            SettingsSection(title = "Appearance") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ThemeOption(
                        title = "Light",
                        isSelected = settings.themeMode == ThemeMode.LIGHT,
                        onClick = { viewModel.updateThemeMode(ThemeMode.LIGHT) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeOption(
                        title = "Dark",
                        isSelected = settings.themeMode == ThemeMode.DARK,
                        onClick = { viewModel.updateThemeMode(ThemeMode.DARK) },
                        modifier = Modifier.weight(1f)
                    )
                    ThemeOption(
                        title = "System",
                        isSelected = settings.themeMode == ThemeMode.SYSTEM,
                        onClick = { viewModel.updateThemeMode(ThemeMode.SYSTEM) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Gradient Background Section
            SettingsSection(title = "Background") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Dynamic Gradient",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Apply premium SaaS background",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = settings.useGradient,
                        onCheckedChange = { viewModel.updateUseGradient(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ElectricViolet
                        )
                    )
                }

                if (settings.useGradient) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Select Palette",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(GradientPalette.values()) { palette ->
                            PaletteOption(
                                palette = palette,
                                isSelected = settings.palette == palette,
                                onClick = { viewModel.updatePalette(palette) }
                            )
                        }
                    }
                }
            }

            SettingsSection(title = "About") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigateToAbout() },
                    color = CardButton
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "About GitCoz",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CardPrimaryText
                            )
                            Text(
                                text = "Version, vision, and developer details",
                                style = MaterialTheme.typography.bodySmall,
                                color = CardSecondaryText
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_forward),
                            contentDescription = "Open About GitCoz",
                            tint = ElectricViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigateToFeedback() },
                    color = CardButton
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Feedback",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CardPrimaryText
                            )
                            Text(
                                text = "Share your ideas and report issues",
                                style = MaterialTheme.typography.bodySmall,
                                color = CardSecondaryText
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_forward),
                            contentDescription = "Open Feedback",
                            tint = ElectricViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Account Section - Logout
            SettingsSection(title = "Account") {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Sign Out",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = ElectricViolet,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ThemeOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) ElectricViolet else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        color = if (isSelected) ElectricViolet.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) ElectricViolet else Color.Gray
            )
        }
    }
}

@Composable
fun PaletteOption(
    palette: GradientPalette,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = when (palette) {
        GradientPalette.AURORA -> listOf(BgGradientStart, BgGradientMid, BgGradientEnd)
        GradientPalette.COSMIC -> listOf(BgGradientDarkStart, BgGradientDarkMid, BgGradientDarkEnd)
        GradientPalette.SUNSET -> listOf(SunsetStart, SunsetMid, SunsetEnd)
        GradientPalette.OCEAN -> listOf(OceanStart, OceanMid, OceanEnd)
        GradientPalette.FOREST -> listOf(ForestStart, ForestMid, ForestEnd)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(colors))
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) ElectricViolet else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = palette.name.lowercase().capitalize(),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) ElectricViolet else Color.Gray
        )
    }
}
