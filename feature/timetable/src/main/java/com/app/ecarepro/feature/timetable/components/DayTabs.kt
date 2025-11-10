package com.app.ecarepro.feature.timetable.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun DayTabs(
    selectedDayIndex: Int,
    days: List<String>,
    onClickDayTabs:(index:Int) -> Unit
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedDayIndex,
        containerColor = White,
        edgePadding = 0.dp,
        minTabWidth = 70.dp
    ) {
        days.forEachIndexed { index, title ->
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
                        text = title,
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
            days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
            onClickDayTabs = {}
        )
    }
}