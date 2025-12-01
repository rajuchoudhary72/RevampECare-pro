package com.app.ecarepro.feature.leave.leave_report.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.LeaveReportItem
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelBottomSheet(
    leave: LeaveReportItem,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.appColors.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Title
            Text(
                text = "Cancel Leave",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.appColors.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Leave Details
            Text(
                text = "Applicant: ${leave.applicantName.ifEmpty { leave.studentName }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Leave Type: ${leave.leaveType}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.textSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Duration: ${leave.durationStr}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.textSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Date: ${leave.fromDate} - ${leave.tillDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.textSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Status: ${leave.status}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Warning Message
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.appColors.error.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Are you sure you want to cancel this approved leave?",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.appColors.error
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This will revoke the leave approval and the applicant will be notified.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.appColors.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cancel Button
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    enabled = !isSubmitting
                ) {
                    Text("Keep Leave")
                }

                // Confirm Cancel Button
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.appColors.error
                    )
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.appColors.surface
                        )
                    } else {
                        Text("Cancel Leave")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CancelBottomSheetPreview() {
    EcareProTheme {
        CancelBottomSheet(
            leave = LeaveReportItem(
                lvID = 1,
                fromDate = "2025-01-15",
                tillDate = "2025-01-17",
                duration = 3.0,
                durationStr = "3 days",
                reason = "Family function",
                leaveType = "Casual Leave",
                leaveAbbr = "CL",
                status = "Approved",
                submittedOn = "2025-01-10",
                actionOn = "2025-01-11",
                rejectionReason = "",
                attachment = "",
                applicantName = "John Doe",
                applicantPhoto = "",
                studentName = "",
                studentPhoto = "",
                studentClass = "",
                designation = "Teacher",
                teacherID = 1,
                teacherName = "John Doe",
                sid = 0,
                photo = "",
                halfdayDTL = null,
                forwardedBy = 0,
                forwardedByName = null,
                cancelby = null,
                cancelledOn = null,
                showCancelButton = true,
                attPer = "",
                isDirector = false
            ),
            isSubmitting = false,
            onDismiss = {},
            onConfirm = {}
        )
    }
}
