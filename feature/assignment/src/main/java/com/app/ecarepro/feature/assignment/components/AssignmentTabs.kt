package com.app.ecarepro.feature.assignment.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.assignment.screens.SubmissionTab

@Composable
fun AssignmentTabs(
    selectedTab: SubmissionTab,
    onTabSelected: (SubmissionTab) -> Unit,
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = SubmissionTab.entries.indexOf(selectedTab),
        containerColor = White,
        edgePadding = 0.dp,
        minTabWidth = 70.dp
    ) {
        SubmissionTab.entries.forEachIndexed { index, data ->
            val isSelected = index == SubmissionTab.entries.indexOf(selectedTab)
            val textStyle =
                if (isSelected) MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp) else MaterialTheme.appTypography.interMedium16px.copy(
                    fontSize = 14.sp
                )
            Tab(
                selected = isSelected,
                onClick = {
                    onTabSelected(SubmissionTab.entries[index])
                },
                text = {
                    Text(
                        text = data.title.orEmpty(),
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
        AssignmentTabs(
            selectedTab = SubmissionTab.SUBMITTED,
            onTabSelected = {}
        )
    }
}