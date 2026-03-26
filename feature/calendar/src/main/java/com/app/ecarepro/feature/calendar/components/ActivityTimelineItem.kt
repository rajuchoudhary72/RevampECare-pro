package com.app.ecarepro.feature.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.activity_calendar.ActivityItem
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

private val HolidayColor = Color(0xFFF44336)

@Composable
fun ActivityTimelineItem(
    activity: ActivityItem,
    isFirst: Boolean,
    isLast: Boolean,
) {
    val accentColor = if (activity.isWorking) {
        MaterialTheme.appColors.primary
    } else {
        HolidayColor
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DateTimelineColumn(
            activity = activity,
            accentColor = accentColor,
            isFirst = isFirst,
            isLast = isLast,
        )

        EventCard(
            title = activity.title,
            accentColor = accentColor,
            isMultiDay = activity.isMultiDay,
        )
    }
}

@Composable
private fun RowScope.DateTimelineColumn(
    activity: ActivityItem,
    accentColor: Color,
    isFirst: Boolean,
    isLast: Boolean,
) {
    Column {
        DateDotPair(
            date = activity.displayDate,
            dayOfWeek = activity.displayDayOfWeek,
            accentColor = accentColor,
            showLineAbove = !isFirst,
            showLineBelow = activity.isMultiDay || !isLast,
        )

        if (activity.isMultiDay) {
            TimelineLineSection(height = 30.dp)
            DateDotPair(
                date = activity.displayTillDate.orEmpty(),
                dayOfWeek = activity.displayTillDayOfWeek.orEmpty(),
                accentColor = accentColor,
                showLineAbove = true,
                showLineBelow = !isLast,
            )
        }

        if (!isLast) {
            TimelineLineSection(height = 40.dp)
        }
    }
}

@Composable
private fun RowScope.EventCard(
    title: String,
    accentColor: Color,
    isMultiDay: Boolean,
) {
    Text(
        text = title,
        style = MaterialTheme.appTypography.interMedium14px,
        color = accentColor,
        modifier = Modifier
            .weight(1f)
            .defaultMinSize(minHeight = if (isMultiDay) 100.dp else Dp.Unspecified)
            .background(
                color = accentColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
    )
}
