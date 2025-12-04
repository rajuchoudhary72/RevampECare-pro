package com.app.ecarepro.feature.assignment.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.core.domain.model.Assignment
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun AssignmentItem(
    assignment: Assignment,
    onViewClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onViewReportClick: () -> Unit,
) {
    val isOverdue = true

    val successColor = Color(0xFF4CAF50) // Green form screenshot
    val warningColor = Color(0xFFFF9800) // Orange from screenshot
    val errorColor = Color(0xFFF44336)   // Red from screenshot

    // Determine status color based on Overdue logic or simply use the flag
    val statusColor = if (isOverdue) errorColor else successColor

    // Calculate Progress
    val progress = 50.0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = assignment.title.orEmpty(),
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary
            )

            Text(
                text = "Submit by ${assignment.submitDate}",
                style = MaterialTheme.appTypography.interMedium16px.copy(fontSize = 12.sp),
                color = if (isOverdue) warningColor else successColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 2. Subtitle: Class • Subject • Date
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = successColor)) {
                    append(assignment.classX)
                }
                append(" • ${assignment.subject} • ${assignment.uploadedOn}")
            },
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Progress Bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = if (isOverdue) warningColor else successColor,
            trackColor = MaterialTheme.appColors.background,
            strokeCap = StrokeCap.Round,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Submission Count Text
        // Logic to flip text positions based on screenshot variation (some have date on left, count on right)
        // For now, following the standard layout in screenshot 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Some items in screenshot show "Submit by..." here, others show count.
            // We will stick to the Count logic for consistency or conditional check.
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = if (isOverdue) warningColor else successColor
                        )
                    ) {
                        append("20")
                    }
                    append(" of 40 students submitted")
                },
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary
            )
        }


        Spacer(modifier = Modifier.height(12.dp))

        // 5. Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (assignment.hasAttachment) {
                ActionButton(
                    iconRes = R.drawable.ic_eye, // Ensure this drawable exists
                    text = "View",
                    onClick = onViewClick
                )

                VerticalDivider()

                // Download Button
                ActionButton(
                    iconRes = R.drawable.ic_download, // Ensure this drawable exists
                    text = "Download",
                    onClick = onDownloadClick
                )
            }


            Spacer(modifier = Modifier.weight(1f))

            // View Report Button
            ActionButton(
                iconRes = com.app.ecarepro.feature.assignment.R.drawable.icon_calendar, // Ensure this drawable exists
                text = "View report",
                onClick = onViewReportClick
            )
        }
    }
}

@Composable
private fun ActionButton(
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            tint = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.appTypography.interMedium16px.copy(fontSize = 12.sp),
            color = MaterialTheme.appColors.textSecondary
        )
    }
}

@Composable
private fun VerticalDivider() {
    Spacer(modifier = Modifier.width(12.dp))
    HorizontalDivider(
        modifier = Modifier
            .height(16.dp)
            .width(1.dp),
        color = MaterialTheme.appColors.border
    )
    Spacer(modifier = Modifier.width(12.dp))
}


@Preview(showBackground = true)
@Composable
private fun AssignmentItemPreview() {
    EcareProTheme {
        AssignmentItem(
            assignment = Assignment(
                id = "1",
                title = "Physics assignment",
                classX = "9th class",
                subject = "English",
                asgDate = "08 Aug 2025",
                uploadedOn = "22 Oct",
                asgFile = null,
                asgFiles = null,
                asgID = null,
                assignmentBy = null,
                hasAttachment = false,
                isActive = null,
                isMine = null,
                lateSubmission = null,
                stIDs = null,
                submitDate = null,
                updateBy = null,
                userID = null,
                userType = null
            ),
            onViewClick = {},
            onDownloadClick = {},
            onViewReportClick = {}
        )
    }
}