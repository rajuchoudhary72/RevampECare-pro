package com.app.ecarepro.feature.leave.appliedleaves.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.leave.appliedleaves.data.LeaveStatus

@Composable
fun LeaveStatusTabs(
    tabs: List<LeaveStatus>,
    selectedTab: LeaveStatus,
    onTabSelected: (LeaveStatus) -> Unit
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = tabs.indexOf(selectedTab).coerceAtLeast(0),
        containerColor = White,
        edgePadding = 0.dp,
        minTabWidth = 90.dp
    ) {
        tabs.forEach { status ->
            val isSelected = status == selectedTab
            val textStyle = if (isSelected) {
                MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp)
            } else {
                MaterialTheme.appTypography.interMedium16px.copy(fontSize = 14.sp)
            }

            Tab(
                selected = isSelected,
                onClick = { onTabSelected(status) },
                text = {
                    Text(
                        text = status.displayText,
                        style = textStyle
                    )
                },
                selectedContentColor = MaterialTheme.appColors.primary,
                unselectedContentColor = MaterialTheme.appColors.textPrimary
            )
        }
    }
}
