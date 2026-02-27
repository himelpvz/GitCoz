package com.hypex.gitcoz.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hypex.gitcoz.ui.theme.CardBottomBg
import com.hypex.gitcoz.ui.theme.CardButton
import com.hypex.gitcoz.ui.theme.CardGradientEnd
import com.hypex.gitcoz.ui.theme.CardGradientStart

@Composable
fun LoadingCardListPlaceholder(
    count: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(count) {
            LoadingCardPlaceholder()
        }
    }
}

@Composable
fun LoadingCardPlaceholder(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "placeholder_transition")
    val alpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "placeholder_alpha"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBottomBg)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(CardGradientStart.copy(alpha = 0.55f * alpha))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CardButton.copy(alpha = alpha))
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardGradientEnd.copy(alpha = 0.2f + (0.4f * alpha)))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardGradientEnd.copy(alpha = 0.15f + (0.35f * alpha)))
                    )
                }
            }
        }
    }
}
