package com.app.ecarepro.feature.assignment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.assignment.screens.StudentSubmissionItem

@Composable
fun StudentRowItem(
    student: StudentSubmissionItem,
    index: Int,
) {
    val backgroundColor = if (index % 2 == 0) Color.White else Color(0xFFF8F8F8)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${student.rollNo}.",
            style = MaterialTheme.appTypography.interRegular12px.copy(color = MaterialTheme.appColors.textSecondary),
            modifier = Modifier.width(50.dp)
        )
        Text(
            text = student.name,
            style = MaterialTheme.appTypography.interMedium16px.copy(
                color = MaterialTheme.appColors.textPrimary,
                fontSize = 12.sp
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = student.submissionMode,
            style = MaterialTheme.appTypography.interMedium16px.copy(
                color = MaterialTheme.appColors.error  ,
                fontSize = 12.sp
            ),
            textAlign = TextAlign.End
        )
    }
}