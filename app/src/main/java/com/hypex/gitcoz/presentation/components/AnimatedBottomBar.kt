package com.hypex.gitcoz.presentation.components

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.hypex.gitcoz.presentation.Screen
import com.hypex.gitcoz.ui.theme.ElectricViolet
import com.hypex.gitcoz.ui.theme.CardStar

import androidx.compose.foundation.background

@Composable
fun AnimatedBottomBar(
    screens: List<Screen>,
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    val selectedIndex = remember(currentRoute) {
        screens.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
    }

    // Column container should be transparent to show the bar's indent/cutout
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedNavigationBar(
            modifier = Modifier.height(64.dp),
            selectedIndex = selectedIndex,
            ballColor = CardStar,
            cornerRadius = shapeCornerRadius(15.dp), 
            ballAnimation = Parabolic(tween(500)),
            indentAnimation = Height(
                indentWidth = 56.dp,
                indentHeight = 15.dp,
                animationSpec = tween(
                    1000,
                    easing = { OvershootInterpolator().getInterpolation(it) })
            ),
            barColor = ElectricViolet
        ) {
            screens.forEach { screen ->
                val isSelected = currentRoute == screen.route
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onNavigate(screen) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.title,
                        modifier = Modifier.size(26.dp),
                        tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
        // Fill the system navigation bar area with the same color as the bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ElectricViolet)
                .navigationBarsPadding()
        )
    }
}
