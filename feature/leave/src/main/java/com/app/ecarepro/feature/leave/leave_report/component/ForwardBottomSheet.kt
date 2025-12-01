package com.app.ecarepro.feature.leave.leave_report.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.ecarepro.core.domain.model.LeaveReportItem
import com.app.ecarepro.core.domain.model.ReportingManager
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardBottomSheet(
    leave: LeaveReportItem,
    reportingManagers: List<ReportingManager>,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (managerId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedManager by remember { mutableStateOf<ReportingManager?>(null) }

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
                text = "Forward Leave",
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

            Spacer(modifier = Modifier.height(16.dp))

            // Reporting Managers List
            Text(
                text = "Select Reporting Manager",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.appColors.textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (reportingManagers.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.appColors.background
                    )
                ) {
                    Text(
                        text = "No reporting managers available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reportingManagers) { manager ->
                        ReportingManagerItem(
                            manager = manager,
                            isSelected = selectedManager?.teacherID == manager.teacherID,
                            onClick = { selectedManager = manager }
                        )
                    }
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
                    Text("Cancel")
                }

                // Forward Button
                Button(
                    onClick = {
                        selectedManager?.let { manager ->
                            onConfirm(manager.teacherID)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isSubmitting && selectedManager != null,
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
                        Text("Forward")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ReportingManagerItem(
    manager: ReportingManager,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.appColors.primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.appColors.background
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.appColors.primary)
            )
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            AsyncImage(
                model = manager.photo,
                contentDescription = "Profile photo of ${manager.teacherName}",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Manager Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = manager.teacherName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.appColors.textPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = manager.designation ?: "Staff",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.appColors.textSecondary
                )
            }

            // Selection Indicator
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForwardBottomSheetPreview() {
    EcareProTheme {
        ForwardBottomSheet(
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
            reportingManagers = listOf(
                ReportingManager(
                    teacherID = 1,
                    teacherName = "Dr. Smith",
                    photo = "",
                    designation = "Principal"
                ),
                ReportingManager(
                    teacherID = 2,
                    teacherName = "Mr. Johnson",
                    photo = "",
                    designation = "Vice Principal"
                )
            ),
            isSubmitting = false,
            onDismiss = {},
            onConfirm = {}
        )
    }
}
