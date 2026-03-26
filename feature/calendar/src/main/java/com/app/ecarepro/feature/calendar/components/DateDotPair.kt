package com.app.ecarepro.feature.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

private val TimelineLineColor = Color(0xFFE0E0E0)

@Composable
fun DateDotPair(
    date: String,
    dayOfWeek: String,
    accentColor: Color,
    showLineAbove: Boolean,
    showLineBelow: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Date + day of week label
        Column(
            modifier = Modifier.width(110.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = date,
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
            )
            Text(
                text = dayOfWeek,
                style = MaterialTheme.appTypography.interRegular13px,
                color = MaterialTheme.appColors.textSecondary,
            )
        }

        // Dot with continuous line running through it
        Box(
            modifier = Modifier.size(22.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Line segments above and below dot center
            Column(modifier = Modifier.fillMaxSize()) {
                if (showLineAbove) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .width(1.5.dp)
                            .background(TimelineLineColor)
                            .align(Alignment.CenterHorizontally),
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                if (showLineBelow) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .width(1.5.dp)
                            .background(TimelineLineColor)
                            .align(Alignment.CenterHorizontally),
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Outer ring (20% opacity)
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(accentColor.copy(alpha = 0.2f), CircleShape),
            )

            // Inner solid dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(accentColor, CircleShape),
            )
        }
    }
}

@Composable
fun TimelineLineSection(height: Dp) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Spacer(modifier = Modifier.width(110.dp))
        Box(
            modifier = Modifier
                .width(1.5.dp)
                .height(height)
                .background(TimelineLineColor),
        )
    }
}
