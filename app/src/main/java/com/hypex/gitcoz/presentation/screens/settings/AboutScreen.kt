package com.hypex.gitcoz.presentation.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hypex.gitcoz.R
import com.hypex.gitcoz.presentation.components.SaaSTopBar
import com.hypex.gitcoz.ui.theme.CardBottomBg
import com.hypex.gitcoz.ui.theme.CardButton
import com.hypex.gitcoz.ui.theme.CardGradientEnd
import com.hypex.gitcoz.ui.theme.CardGradientStart
import com.hypex.gitcoz.ui.theme.CardPrimaryText
import com.hypex.gitcoz.ui.theme.CardSecondaryText
import com.hypex.gitcoz.ui.theme.ElectricViolet
import com.hypex.gitcoz.ui.theme.JetBrainsMonoFamily
import com.hypex.gitcoz.ui.theme.OceanMid

private const val GITHUB_AVATAR_URL = "https://avatars.githubusercontent.com/u/245136080?v=4"
private const val TELEGRAM_PROFILE_IMAGE_URL = "https://cdn5.telesco.pe/file/" +
    "RK1kTlPE_16vNLkSrPLQBQC7Yus2ksHMHUsOV_7IgDPp1m3ItidMvxLZv5WgdID9UtdCLs4qY1_09H9lDJGp9" +
    "ckygeIzZiD8bh9JKoSgi0qVqJHd4wywhJOcSxFWIdRnNKt_ers8J7kaQ7GcCI3MM_UbHkgMyne4phueO29OF6" +
    "AJwSJLdW-dNbNI9aYXdm3iapvyJy4n3iihXjmEnL2hxD5RhLMyw2_YF2U_Sykb2ORRMzbPmfN7FtjS1eOpgHG0" +
    "YIgfJiVK7zNIetyb_dXAJxwY47Z5naDrth_zuS6nUa0dfaHr7wWxyuZ-ZrosRxbqY3UbunLhMeBUqk0GJkvdUA.jpg"
private const val TELEGRAM_GROUP_IMAGE_URL = "https://cdn5.telesco.pe/file/" +
    "drCNvMsxrinlXeHHSjz8ILU97QiRaqELmWjYlvMwTrEQK-4_iFP3omqAcRYwyqKi-JxUu8fj5nIrQ6L2G4gkxz" +
    "qtBgK_ihI2FA71p90ThkKKQ7vByOZAnHmwDaQ86YzDzoXmUyWH03i_RqSVIZl9yMbfgr7br8L4ceALvon0oQks" +
    "PO7pPQ2DoD_-rje46hGFZn7UYJQ8IV6uJDiNkoT9KKF-uw7EvMYd5viJSSzkIp5j5rVfsHggUKOHCDRkwkZ0uq" +
    "gJp-V3sP0FPV7G5F81I8nVcVSdBPzLV6mi0xxN5uKNAom9ckgb5jv6jRPwI3nBLd_JGVJZoThIeGU_G4I7kQ.jpg"

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        SaaSTopBar(
            title = "About GitCoz",
            subtitle = "Crafted for modern developers",
            actions = {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeroAboutCard()
            PremiumInfoCard(
                title = "Vision",
                body = "GitCoz helps you discover repositories, profiles, and releases " +
                    "in a premium mobile experience."
            )
            PremiumInfoCard(
                title = "Build",
                body = "Kotlin + Jetpack Compose + Retrofit with clean architecture."
            )
            ProfileSourceCard(
                source = "GitHub",
                imageUrl = GITHUB_AVATAR_URL,
                fullName = "Himel Parvez",
                handle = "@himelpvz",
                bio = "No bio set on GitHub yet.",
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/himelpvz"))
                    )
                }
            )
            ProfileSourceCard(
                source = "Telegram",
                imageUrl = TELEGRAM_PROFILE_IMAGE_URL,
                fullName = "Exx Hypx (sunstone)",
                handle = "@Himel_Pvz",
                bio = "@himels_bio .Too real to be fake.",
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Himel_Pvz"))
                    )
                }
            )
            ProfileSourceCard(
                source = "Support Channel",
                imageUrl = TELEGRAM_GROUP_IMAGE_URL,
                fullName = "HIMEL'S BUILD",
                handle = "@hpsstuffs",
                bio = "Share activities, experiments, coding, mods, guides, and project updates.",
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/hpsstuffs"))
                    )
                }
            )
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun HeroAboutCard() {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardBottomBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = CardGradientStart.copy(alpha = 0.26f),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(CardGradientStart, OceanMid, CardGradientEnd)
                    )
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.18f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_ai_sparkle),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Text(
                    text = "PREMIUM GITHUB EXPLORER",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = JetBrainsMonoFamily,
                    color = Color.White.copy(alpha = 0.75f)
                )
                Text(
                    text = "GitCoz",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "A stylish GitHub companion for discovery and tracking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.86f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PremiumTag(text = "Compose UI")
                    PremiumTag(text = "Fast Search")
                    PremiumTag(text = "Releases")
                }
            }
        }
    }
}

@Composable
private fun PremiumInfoCard(title: String, body: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBottomBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = CardGradientStart.copy(alpha = 0.18f),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = ElectricViolet,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CardButton)
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CardSecondaryText,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun PremiumTag(text: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.18f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}

@Composable
private fun ProfileSourceCard(
    source: String,
    imageUrl: String,
    fullName: String,
    handle: String,
    bio: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBottomBg),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = CardGradientStart.copy(alpha = 0.2f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = source,
                style = MaterialTheme.typography.labelLarge,
                color = ElectricViolet,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = fullName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = fullName,
                        style = MaterialTheme.typography.titleMedium,
                        color = CardPrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = handle,
                        style = MaterialTheme.typography.labelLarge,
                        color = ElectricViolet,
                        fontFamily = JetBrainsMonoFamily
                    )
                }
            }
            Text(
                text = bio,
                style = MaterialTheme.typography.bodySmall,
                color = CardSecondaryText
            )
        }
    }
}
