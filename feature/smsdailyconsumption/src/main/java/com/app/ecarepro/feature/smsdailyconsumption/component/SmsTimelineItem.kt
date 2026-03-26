package com.app.ecarepro.feature.smsdailyconsumption.component

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.sms.DailyConsumptionItem
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.smsdailyconsumption.R

private val TimelineLineColor = Color(0xFFE0E0E0)

@Composable
fun SmsTimelineItem(
    item: DailyConsumptionItem,
    isFirst: Boolean,
    isLast: Boolean,
) {
    val dotColor = MaterialTheme.appColors.primary

    Row(
        modifier = Modifier
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
        text = "${stringResource(R.string.sms_dailyConsumption_smsCount)} $count",
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

@Composable
fun SmsTimelineConnector() {
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
