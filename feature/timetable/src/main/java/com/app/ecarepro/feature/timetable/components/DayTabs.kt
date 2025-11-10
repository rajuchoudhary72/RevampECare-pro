package com.app.ecarepro.feature.timetable.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.TimetableData
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun DayTabs(
    selectedDayIndex: Int,
    days: List<TimetableData>,
    onClickDayTabs: (index: Int) -> Unit,
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedDayIndex,
        containerColor = White,
        edgePadding = 0.dp,
        minTabWidth = 70.dp
    ) {
        days.forEachIndexed { index, data ->
            val isSelected = index == selectedDayIndex
            val textStyle =
                if (isSelected) MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp) else MaterialTheme.appTypography.interMedium16px.copy(
                    fontSize = 14.sp
                )
            Tab(
                selected = isSelected,
                onClick = {
                    onClickDayTabs(index)
                },
                text = {
                    Text(
                        text = data.day.orEmpty(),
                        style = textStyle,
                    )
                },
                selectedContentColor = MaterialTheme.appColors.primary,
                unselectedContentColor = MaterialTheme.appColors.textPrimary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DayTabsPreview() {
    EcareProTheme {
        DayTabs(
            selectedDayIndex = 1,
            days = listOf(
                TimetableData(day = "Day 1", dayNo = 1, timeTable = emptyList()),
                TimetableData(day = "Day 2", dayNo = 1, timeTable = emptyList()),
                TimetableData(day = "Day 3", dayNo = 1, timeTable = emptyList()),
                TimetableData(day = "Day 4", dayNo = 1, timeTable = emptyList()),

                ),
            onClickDayTabs = {}
        )
    }
}