package com.app.ecarepro.feature.assignment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.Assignment
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun AssignmentDetailsTopBar(
    assignment: Assignment,
    onCloseClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = assignment.title.orEmpty(),
                    style = MaterialTheme.appTypography.interMedium16px,
                    color = MaterialTheme.appColors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.appColors.success)) {
                            append(assignment.classX)
                        }
                        append(" • ${assignment.subject} • ${assignment.uploadedOn}")
                    },
                    style = MaterialTheme.appTypography.interMedium16px.copy(fontSize = 12.sp),
                    color = MaterialTheme.appColors.textSecondary
                )
            }
            IconButton(
                onClick = onCloseClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AssignmentDetailsTopBarPreview() {
    EcareProTheme {
        AssignmentDetailsTopBar(
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
            onCloseClick = {}
        )
    }
}


