package com.app.ecarepro.feature.leave.appliedleaves.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun LeaveTimeline(
    fromDate: String,
    toDate: String,
    duration: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // From section
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = "From",
                style = MaterialTheme.appTypography.interRegular12px.copy(
                    color = Color(0xFF00BCD4),
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = fromDate,
                style = MaterialTheme.appTypography.interMedium16px.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.appColors.textPrimary
            )
        }

        // Timeline with dashed line
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)

            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw dashed line
                drawLine(
                    color = Color(0xFF00BCD4),
                    start = Offset(0f, canvasHeight / 2),
                    end = Offset(canvasWidth, canvasHeight / 2),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))
                )

                // Left dot
                drawCircle(
                    color = Color(0xFF00BCD4),
                    radius = 4.dp.toPx(),
                    center = Offset(0f, canvasHeight / 2)
                )

                // Right dot
                drawCircle(
                    color = Color(0xFF00BCD4),
                    radius = 4.dp.toPx(),
                    center = Offset(canvasWidth, canvasHeight / 2)
                )
            }

            // Duration text in center
            Text(
                text = duration,
                style = MaterialTheme.appTypography.interMedium16px.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.appColors.textPrimary,
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 8.dp)
            )
        }

        // To section
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "To",
                style = MaterialTheme.appTypography.interRegular12px.copy(
                    color = Color(0xFF00BCD4),
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = toDate,
                style = MaterialTheme.appTypography.interMedium16px.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.appColors.textPrimary
            )
        }
    }
}
