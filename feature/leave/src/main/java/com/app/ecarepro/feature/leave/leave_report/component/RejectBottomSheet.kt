package com.app.ecarepro.feature.leave.leave_report.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.LeaveReportItem
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RejectBottomSheet(
    leave: LeaveReportItem,
    isRejectionReasonRequired: Boolean,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (reason: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var rejectionReason by remember { mutableStateOf("") }
    val isValid = !isRejectionReasonRequired || rejectionReason.isNotBlank()

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
                text = "Reject Leave",
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

            Spacer(modifier = Modifier.height(16.dp))

            // Rejection Reason (conditionally required)
            OutlinedTextField(
                value = rejectionReason,
                onValueChange = { rejectionReason = it },
                label = {
                    Text(
                        if (isRejectionReasonRequired) "Rejection Reason *" else "Rejection Reason (Optional)"
                    )
                },
                placeholder = { Text("Enter reason for rejection") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5,
                isError = isRejectionReasonRequired && rejectionReason.isBlank(),
                supportingText = {
                    if (isRejectionReasonRequired && rejectionReason.isBlank()) {
                        Text(
                            text = "Rejection reason is required",
                            color = MaterialTheme.appColors.error
                        )
                    }
                }
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
                Text(
                    text = "Are you sure you want to reject this leave application? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.error,
                    modifier = Modifier.padding(16.dp)
                )
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
                    Text("Cancel")
                }

                // Confirm Reject Button
                Button(
                    onClick = {
                        if (isValid) {
                            onConfirm(rejectionReason.ifBlank { null })
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isSubmitting && isValid,
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
                        Text("Reject")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RejectBottomSheetPreview() {
    EcareProTheme {
        RejectBottomSheet(
            leave = LeaveReportItem(
                lvID = 1,
                fromDate = "2025-01-15",
                tillDate = "2025-01-17",
                duration = 3.0,
                durationStr = "3 days",
                reason = "Family function",
                leaveType = "Casual Leave",
                leaveAbbr = "CL",
                status = "Pending",
                submittedOn = "2025-01-10",
                actionOn = "",
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
                showCancelButton = false,
                attPer = "",
                isDirector = false
            ),
            isRejectionReasonRequired = true,
            isSubmitting = false,
            onDismiss = {},
            onConfirm = {}
        )
    }
}
