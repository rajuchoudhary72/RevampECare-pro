package com.app.ecarepro.feature.assignment.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.assignment.Assignment

@Composable
fun AssignmentList(
    modifier: Modifier = Modifier,
    assignments: List<Assignment>,
    onViewClick: (Assignment) -> Unit,
    onDownloadClick: (Assignment) -> Unit,
    onViewReportClick: (Assignment) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(assignments, key = { it.id }) { assignment ->
            AssignmentItem(
                assignment = assignment,
                onViewClick = { onViewClick(assignment) },
                onDownloadClick = { onDownloadClick(assignment) },
                onViewReportClick = { onViewReportClick(assignment) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.appColors.border
            )
        }
    }
}