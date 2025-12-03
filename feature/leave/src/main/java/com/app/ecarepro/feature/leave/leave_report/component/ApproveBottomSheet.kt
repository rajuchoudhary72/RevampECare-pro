package com.app.ecarepro.feature.leave.leave_report.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.LeaveReportItem
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApproveBottomSheet(
    leave: LeaveReportItem,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (startDate: String?, endDate: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPartialApproval by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf(leave.fromDate) }
    var selectedEndDate by remember { mutableStateOf(leave.tillDate) }

    val isMultiDayLeave = remember(leave) {
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fromDate = LocalDate.parse(leave.fromDate, formatter)
            val tillDate = LocalDate.parse(leave.tillDate, formatter)
            ChronoUnit.DAYS.between(fromDate, tillDate) > 0
        } catch (e: Exception) {
            false
        }
    }

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
                text = "Approve Leave",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.appColors.textPrimary
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

            // Partial Approval Toggle (only for multi-day leaves)
            if (isMultiDayLeave) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showPartialApproval,
                        onCheckedChange = { showPartialApproval = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Partial Approval",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.appColors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Date Range Selection (only when partial approval is enabled)
            if (showPartialApproval) {
                Text(
                    text = "Select Date Range for Approval",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.appColors.textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Start Date Field
                    OutlinedTextField(
                        value = selectedStartDate,
                        onValueChange = {},
                        label = { Text("Start Date") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Select Start Date"
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                // TODO: Open date picker
                            }
                    )

                    // End Date Field
                    OutlinedTextField(
                        value = selectedEndDate,
                        onValueChange = {},
                        label = { Text("End Date") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Select End Date"
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                // TODO: Open date picker
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Confirmation Message
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.appColors.background
                )
            ) {
                Text(
                    text = if (showPartialApproval) {
                        "Are you sure you want to approve leave from $selectedStartDate to $selectedEndDate?"
                    } else {
                        "Are you sure you want to approve this leave application?"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textPrimary,
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

                // Confirm Button
                Button(
                    onClick = {
                        if (showPartialApproval) {
                            onConfirm(selectedStartDate, selectedEndDate)
                        } else {
                            onConfirm(null, null)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.appColors.primary
                    )
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.appColors.surface
                        )
                    } else {
                        Text("Approve")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApproveBottomSheetPreview() {
    EcareProTheme {
        ApproveBottomSheet(
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
            isSubmitting = false,
            onDismiss = {},
            onConfirm = { _, _ -> }
        )
    }
}
