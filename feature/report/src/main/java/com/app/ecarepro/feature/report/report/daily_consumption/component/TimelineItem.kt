package com.app.ecarepro.feature.report.report.daily_consumption.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.sms.DailyConsumptionItem
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

private val TimelineLineColor = Color(0xFFE0E0E0)

@Composable
fun TimelineItem(
    item: DailyConsumptionItem,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    val dotColor = MaterialTheme.appColors.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DateDotColumn(
            displayDate = item.displayDate,
            displayDayOfWeek = item.displayDayOfWeek,
            dotColor = dotColor,
            isFirst = isFirst,
            isLast = isLast,
        )

        SmsCountCard(count = item.count)
    }
}

@Composable
fun TimelineConnector() {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(modifier = Modifier.width(110.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .width(1.5.dp)
                .height(40.dp)
                .background(TimelineLineColor),
        )
    }
}

@Composable
private fun RowScope.DateDotColumn(
    displayDate: String,
    displayDayOfWeek: String,
    dotColor: Color,
    isFirst: Boolean,
    isLast: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.width(110.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = displayDate,
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
            )
            Text(
                text = displayDayOfWeek,
                style = MaterialTheme.appTypography.interRegular13px,
                color = MaterialTheme.appColors.textSecondary,
            )
        }

        Box(
            modifier = Modifier.size(22.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isFirst) {
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .width(1.5.dp)
                            .background(TimelineLineColor)
                            .align(Alignment.CenterHorizontally),
                    )
                }
                if (isLast) {
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .width(1.5.dp)
                            .background(TimelineLineColor)
                            .align(Alignment.CenterHorizontally),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(dotColor.copy(alpha = 0.2f), CircleShape),
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(dotColor, CircleShape),
            )
        }
    }
}

@Composable
private fun RowScope.SmsCountCard(count: Int) {
    Text(
        text = "SMS Count: $count",
        style = MaterialTheme.appTypography.interMedium14px,
        color = MaterialTheme.appColors.textPrimary,
        modifier = Modifier
            .weight(1f)
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
    )
}

@Preview(showBackground = true, name = "Timeline Item - First")
@Composable
private fun PreviewTimelineItemFirst() {
    EcareProTheme {
        TimelineItem(
            item = DailyConsumptionItem(
                sentOn = "21-Mar-2026",
                displayDate = "21 March",
                displayDayOfWeek = "Saturday",
                count = 1,
            ),
            isFirst = true,
            isLast = false,
        )
    }
}

@Preview(showBackground = true, name = "Timeline Item - Middle")
@Composable
private fun PreviewTimelineItemMiddle() {
    EcareProTheme {
        TimelineItem(
            item = DailyConsumptionItem(
                sentOn = "20-Mar-2026",
                displayDate = "20 March",
                displayDayOfWeek = "Friday",
                count = 6,
            ),
            isFirst = false,
            isLast = false,
        )
    }
}

@Preview(showBackground = true, name = "Timeline Item - Last")
@Composable
private fun PreviewTimelineItemLast() {
    EcareProTheme {
        TimelineItem(
            item = DailyConsumptionItem(
                sentOn = "19-Mar-2026",
                displayDate = "19 March",
                displayDayOfWeek = "Thursday",
                count = 11,
            ),
            isFirst = false,
            isLast = true,
        )
    }
}
