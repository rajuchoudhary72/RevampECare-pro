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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onMenuClick: () -> Unit,
) {

    val totalStudents = assignment.totalStudents ?: 0
    val totalSubmitted = assignment.totalSubmitted ?: 0

    val progress = if (totalStudents > 0) {
        (totalSubmitted.toFloat() / totalStudents) * 100
    } else 0f

    val progressColor = when {
        progress <= 25 -> MaterialTheme.appColors.error
        progress <= 75 -> MaterialTheme.appColors.warning
        else -> MaterialTheme.appColors.success
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = assignment.title.orEmpty(),
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary
            )

            Text(
                text = "Submit by ${assignment.submitDate}",
                style = MaterialTheme.appTypography.interMedium16px.copy(fontSize = 12.sp),
                color = progressColor
            )

            if (assignment.isMine == true)
                IconButton(
                    modifier = Modifier
                        .height(22.dp)
                        .width(32.dp)
                        .padding(start = 10.dp),
                    onClick = onMenuClick
                ) {
                    Icon(
                        painterResource(R.drawable.ic_hori_menu),
                        contentDescription = "More options"
                    )
                }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 2. Subtitle: Class • Subject • Date
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = progressColor)) {
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
            progress = { progress/100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = progressColor,
            trackColor = MaterialTheme.appColors.border,
            strokeCap = StrokeCap.Round,
            drawStopIndicator = {}
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = progressColor
                        )
                    ) {
                        append(assignment.totalSubmitted.toString())
                    }
                    append(" of ${assignment.totalStudents} students submitted")
                },
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary
            )
        }


        Spacer(modifier = Modifier.height(12.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (assignment.hasAttachment) {
                ActionButton(
                    iconRes = R.drawable.ic_eye,
                    text = "View",
                    onClick = onViewClick
                )

                VerticalDivider()

                // Download Button
                ActionButton(
                    iconRes = R.drawable.ic_download,
                    text = "Download",
                    onClick = onDownloadClick
                )
            }


            Spacer(modifier = Modifier.weight(1f))

            // View Report Button
            ActionButton(
                iconRes = com.app.ecarepro.feature.assignment.R.drawable.icon_calendar,
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
                isMine = true,
                lateSubmission = null,
                stIDs = null,
                submitDate = null,
                updateBy = null,
                userID = null,
                userType = null,
                totalStudents = 20,
                totalSubmitted = 5
            ),
            onViewClick = {},
            onDownloadClick = {},
            onViewReportClick = {},
            onMenuClick = {}
        )
    }
}