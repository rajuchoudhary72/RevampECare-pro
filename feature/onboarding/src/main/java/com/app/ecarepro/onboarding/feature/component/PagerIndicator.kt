package com.app.ecarepro.onboarding.feature.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White

internal val IndicatorInactiveColor = Color(0x66E0E0E0)

@Composable
fun PagerIndicator(
    count: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = White,
    inactiveColor: Color = IndicatorInactiveColor
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until count) {
            val isActive = i == currentPage
            // Animate the color of the indicator
            val color by animateColorAsState(
                targetValue = if (isActive) activeColor else inactiveColor,
                label = "IndicatorColorAnimation" // Optional label for debugging
            )
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(height = 6.dp, width = 24.dp)
            )
        }
    }
}

@Preview()
@Composable
fun PagerIndicatorPreview() {
    EcareProTheme {
        PagerIndicator(
            count = 4,
            currentPage = 1,
        )
    }
}

