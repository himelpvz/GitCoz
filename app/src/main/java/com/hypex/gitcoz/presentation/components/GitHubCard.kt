package com.hypex.gitcoz.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hypex.gitcoz.R
import com.hypex.gitcoz.ui.theme.*

@Composable
fun GitHubCard(
    title: String,
    subtitle: String,
    statusText: String?,
    avatarUrl: String?,
    label1: String,
    value1: String,
    label2: String,
    value2: String,
    buttonText: String,
    isPremium: Boolean = true,
    isRepo: Boolean = false,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = CardBottomBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = if (isPremium || isRepo) BorderStroke(1.dp, CardGradientStart.copy(alpha = 0.3f)) else null
    ) {
        Column {
            // Header Section with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(CardGradientStart, CardGradientEnd)
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                color = CardTitle,
                                style = MaterialTheme.typography.titleLarge
                            )
                            if (isPremium || isRepo) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = CardTitle.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        if (isRepo) "REPO" else "PRO",
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CardTitle,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                        Text(
                            text = subtitle,
                            color = CardTitle.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (statusText != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                color = CardTitle.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(CardStar)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = statusText,
                                        color = CardTitle,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                    if (avatarUrl != null) {
                        Surface(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            color = CardTitle.copy(alpha = 0.2f),
                            border = BorderStroke(2.dp, CardTitle.copy(alpha = 0.5f))
                        ) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Info Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBottomBg)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = label1,
                        color = CardSecondaryText,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = value1,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.Bold,
                            color = CardPrimaryText
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = label2,
                        color = CardSecondaryText,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = value2,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.Bold,
                            color = CardPrimaryText
                        )
                    )
                }
            }

            // Action Button
            Button(
                onClick = onButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CardButton,
                    contentColor = CardButtonText
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_card_launch),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
