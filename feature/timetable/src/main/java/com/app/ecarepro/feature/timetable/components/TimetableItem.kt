package com.app.ecarepro.feature.timetable.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.Timetable
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun TimetableItem(entry: Timetable) {
    val backgroundColor =
        if (entry.isCurrentPeriod?:false) MaterialTheme.appColors.primary.copy(alpha = 0.1f) else Color.Transparent
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = MaterialTheme.shapes.extraSmall.copy(CornerSize(0.dp))
    ) {
        Row(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Period
            Column(
                modifier = Modifier.weight(0.18f),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = entry.period.toString(),
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        color = MaterialTheme.appColors.primary,
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 1.dp),
                        text = getOrdinal(number = entry.period?: 1),
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.primary,
                    )
                }
                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Period",
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }
            // Class Info
            Column(
                modifier = Modifier
                    .weight(0.25f)
            ) {
                Text(
                    text = entry.className.orEmpty(),
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = MaterialTheme.appColors.textPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = entry.subject.orEmpty(),
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }
            // Time Info
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = entry.time.orEmpty(),
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = MaterialTheme.appColors.textPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = entry.duration.orEmpty(),
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }
        }
    }
}

private fun getOrdinal(number: Int): String {
    return when {
        // Handle special cases for 11, 12, 13
        number % 100 in 11..13 -> "th"
        // Handle 1, 2, 3
        number % 10 == 1 -> "st"
        number % 10 == 2 -> "nd"
        number % 10 == 3 -> "rd"
        // All other numbers use "th"
        else -> "th"
    }
}

@Preview(showBackground = true)
@Composable
private fun TimetableItemPreview() {
    EcareProTheme() {
        TimetableItem(
            entry = Timetable(
                period = 3,
                className = "11-A",
                subject = "Business studies",
                time = "09:30 AM - 10:30 AM",
                duration = "60 mins",
                isCurrentPeriod = false
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimetableItemPreviewForCurrentPeriod() {
    EcareProTheme() {
        TimetableItem(
            entry = Timetable(
                period = 3,
                className = "11-A",
                subject = "Business studies",
                time = "09:30 AM - 10:30 AM",
                duration = "60 mins",
                isCurrentPeriod = true
            )
        )
    }
}