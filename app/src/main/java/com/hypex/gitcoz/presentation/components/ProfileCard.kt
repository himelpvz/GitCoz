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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hypex.gitcoz.R
import com.hypex.gitcoz.domain.model.GitHubUser
import com.hypex.gitcoz.ui.theme.*

@Composable
fun ProfileCard(
    user: GitHubUser,
    onViewProfile: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = CardBottomBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, CardGradientStart.copy(alpha = 0.3f))
    ) {
        Column {
            // Gradient Header with Avatar + Name
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Surface(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape),
                        color = CardTitle.copy(alpha = 0.2f),
                        border = BorderStroke(3.dp, CardTitle.copy(alpha = 0.6f))
                    ) {
                        AsyncImage(
                            model = user.avatarUrl,
                            contentDescription = user.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name + Badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = CardTitle
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = CardTitle.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "PRO",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CardTitle,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Username
                    Text(
                        text = "@${user.login}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CardTitle.copy(alpha = 0.7f)
                    )

                    // Bio
                    if (user.bio.isNotEmpty() && user.bio != "No bio available") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = user.bio,
                            style = MaterialTheme.typography.bodySmall,
                            color = CardTitle.copy(alpha = 0.8f),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBottomBg)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = user.followers.toString(), label = "Followers")
                StatItem(value = user.following.toString(), label = "Following")
                StatItem(value = user.publicRepos.toString(), label = "Repos")
                StatItem(value = user.publicGists.toString(), label = "Gists")
            }

            // Details Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBottomBg)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (user.company.isNotEmpty()) {
                    DetailRow(
                        iconRes = R.drawable.ic_nav_profile,
                        label = "Company",
                        value = user.company
                    )
                }
                if (user.location.isNotEmpty()) {
                    DetailRow(
                        iconRes = R.drawable.ic_search,
                        label = "Location",
                        value = user.location
                    )
                }
                if (user.blog.isNotEmpty()) {
                    DetailRow(
                        iconRes = R.drawable.ic_card_launch,
                        label = "Blog",
                        value = user.blog
                    )
                }
                if (user.twitterUsername.isNotEmpty()) {
                    DetailRow(
                        iconRes = R.drawable.ic_nav_trending,
                        label = "Twitter",
                        value = "@${user.twitterUsername}"
                    )
                }
                if (user.createdAt.isNotEmpty()) {
                    DetailRow(
                        iconRes = R.drawable.ic_calendar,
                        label = "Joined",
                        value = formatJoinDate(user.createdAt)
                    )
                }
            }

            // Action Button
            if (onViewProfile != null) {
                Button(
                    onClick = onViewProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 8.dp),
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
                            text = "View Full Profile",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold
            ),
            color = CardGradientStart
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = CardSecondaryText
        )
    }
}

@Composable
private fun DetailRow(iconRes: Int, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = CardGradientStart
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = CardSecondaryText,
            modifier = Modifier.width(72.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = CardPrimaryText,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun formatJoinDate(isoDate: String): String {
    return try {
        val parts = isoDate.split("T").first().split("-")
        val months = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        val year = parts[0]
        val month = months[parts[1].toInt() - 1]
        val day = parts[2].toInt()
        "$month $day, $year"
    } catch (e: Exception) {
        isoDate
    }
}
