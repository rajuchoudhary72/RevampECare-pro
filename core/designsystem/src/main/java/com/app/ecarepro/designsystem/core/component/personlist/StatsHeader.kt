package com.app.ecarepro.designsystem.core.component.personlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

/**
 * StatsHeader - Shows total count and gender breakdown
 *
 * Design specs:
 * - Background: Light gray (#F2F2F7)
 * - Female icon color: #E91E63 (Pink)
 * - Male icon color: #2196F3 (Blue)
 * - Icon sizes: 12-14sp
 * - Count font: 14sp SemiBold
 * - Label font: 14sp Regular
 * - Spacing between icon and count: 6dp
 * - Spacing between stats groups: 16dp
 * - Separator dot size: 4dp
 */
@Composable
fun StatsHeader(
    stats: ListStatsPresentation,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F2F7))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Leading content (e.g., Total count or Scholar type dropdown)
        if (leadingContent != null) {
            leadingContent()
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.appColors.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${stats.total} Total",
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = MaterialTheme.appColors.textPrimary
                )
            }
        }

        // Gender Stats
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Female/Girls count
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFFE91E63) // Pink
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${stats.femaleCount} ${stats.femaleLabel}",
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Separator dot
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        MaterialTheme.appColors.textSecondary.copy(alpha = 0.5f),
                        CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Male/Boys count
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFF2196F3) // Blue
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${stats.maleCount} ${stats.maleLabel}",
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsHeaderPreview() {
    EcareProTheme {
        StatsHeader(
            stats = ListStatsPresentation(
                total = 934,
                maleCount = 467,
                femaleCount = 467,
                maleLabel = "Boys",
                femaleLabel = "Girls"
            )
        )
    }
}
