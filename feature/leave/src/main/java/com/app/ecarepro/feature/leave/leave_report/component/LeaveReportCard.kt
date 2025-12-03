package com.app.ecarepro.feature.leave.leave_report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.ApplType
import com.app.ecarepro.core.domain.model.LeaveApplication
import com.app.ecarepro.core.domain.model.LeaveReportItem
import com.app.ecarepro.core.domain.model.LeaveReportTab
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.leave.component.LeaveCard

@Composable
fun LeaveReportCard(
    leave: LeaveReportItem,
    applType: ApplType,
    selectedTab: LeaveReportTab,
    isSelected: Boolean,
    showCheckbox: Boolean,
    showAttendancePercentage: Boolean,
    onCheckboxChanged: (Boolean) -> Unit,
    onApproveClicked: () -> Unit,
    onRejectClicked: () -> Unit,
    onForwardClicked: () -> Unit,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Convert LeaveReportItem to LeaveApplication for the existing LeaveCard
    val leaveApplication = LeaveApplication(
        lvID = leave.lvID,
        fromDate = leave.fromDate,
        tillDate = leave.tillDate,
        duration = leave.duration,
        durationStr = leave.durationStr,
        reason = leave.reason,
        leaveType = leave.leaveType,
        leaveAbbr = leave.leaveAbbr,
        status = leave.status,
        submittedOn = leave.submittedOn,
        actionOn = leave.actionOn,
        rejectionReason = leave.rejectionReason,
        attachment = leave.attachment,
        applicantName = leave.applicantName,
        applicantPhoto = leave.applicantPhoto,
        studentName = leave.studentName,
        studentPhoto = leave.studentPhoto,
        studentClass = leave.studentClass,
        designation = leave.designation,
        teacherID = leave.teacherID,
        teacherName = leave.teacherName,
        sid = leave.sid,
        photo = leave.photo,
        halfdayDTL = leave.halfdayDTL,
        forwardedBy = leave.forwardedBy,
        forwardedByName = leave.forwardedByName,
        cancelby = leave.cancelby,
        cancelledOn = leave.cancelledOn,
        showCancelButton = leave.showCancelButton,
        attPer = leave.attPer
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Checkbox + Card Row (for student lists)
        if (showCheckbox) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Leave Card
                Box(modifier = Modifier.weight(1f)) {
                    LeaveCard(
                        leave = leaveApplication,
                        onClick = {},
                        onDeleteClick = {},
                        onApproveClick = if (selectedTab == LeaveReportTab.PENDING) onApproveClicked else null,
                        onRejectClick = if (selectedTab == LeaveReportTab.PENDING) onRejectClicked else null,
                        onForwardClick = if (selectedTab == LeaveReportTab.PENDING && applType == ApplType.STAFF) onForwardClicked else null,
                        showActionButtons = false // Hide action buttons when checkbox is shown
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Checkbox
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onCheckboxChanged,
                    modifier = Modifier.padding(top = 0.dp)
                )
            }
        } else {
            // Regular card without checkbox
            LeaveCard(
                leave = leaveApplication,
                onClick = {},
                onDeleteClick = {},
                onApproveClick = if (selectedTab == LeaveReportTab.PENDING) onApproveClicked else null,
                onRejectClick = if (selectedTab == LeaveReportTab.PENDING) onRejectClicked else null,
                onForwardClick = if (selectedTab == LeaveReportTab.PENDING && applType == ApplType.STAFF) onForwardClicked else null,
                showActionButtons = selectedTab == LeaveReportTab.PENDING // Show action buttons only in Pending tab
            )
        }

        // Attendance Percentage (if enabled for students)
        if (showAttendancePercentage && applType == ApplType.STUDENT && leave.attPer.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.appColors.primary.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Attendance:",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = MaterialTheme.appColors.textSecondary
                    )
                    Text(
                        text = "${leave.attPer}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.appColors.primary
                    )
                }
            }
        }

        // Cancel Leave Button (for Approved tab)
        if (selectedTab == LeaveReportTab.APPROVED && leave.showCancelButton && !showCheckbox) {
            OutlinedButton(
                onClick = onCancelClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.appColors.error
                )
            ) {
                Text("Cancel Leave")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Divider
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = Color(0xFFEEEEEE),
            thickness = 1.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LeaveReportCardPreview() {
    EcareProTheme {
        LeaveReportCard(
            leave = LeaveReportItem(
                lvID = 1,
                fromDate = "2025-01-15",
                tillDate = "2025-01-17",
                duration = 3.0,
                durationStr = "3",
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
                studentName = "Student Name",
                studentPhoto = "",
                studentClass = "Class 10-A",
                designation = "Teacher",
                teacherID = 1,
                teacherName = "John Doe",
                sid = 1,
                photo = "",
                halfdayDTL = null,
                forwardedBy = 0,
                forwardedByName = null,
                cancelby = null,
                cancelledOn = null,
                showCancelButton = false,
                attPer = "85.5",
                isDirector = false
            ),
            applType = ApplType.STAFF,
            selectedTab = LeaveReportTab.PENDING,
            isSelected = false,
            showCheckbox = false,
            showAttendancePercentage = false,
            onCheckboxChanged = {},
            onApproveClicked = {},
            onRejectClicked = {},
            onForwardClicked = {},
            onCancelClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LeaveReportCardWithCheckboxPreview() {
    EcareProTheme {
        LeaveReportCard(
            leave = LeaveReportItem(
                lvID = 1,
                fromDate = "2025-01-15",
                tillDate = "2025-01-17",
                duration = 3.0,
                durationStr = "3",
                reason = "Family function",
                leaveType = "Casual Leave",
                leaveAbbr = "CL",
                status = "Pending",
                submittedOn = "2025-01-10",
                actionOn = "",
                rejectionReason = "",
                attachment = "",
                applicantName = "",
                applicantPhoto = "",
                studentName = "John Smith",
                studentPhoto = "",
                studentClass = "Class 10-A",
                designation = "",
                teacherID = 0,
                teacherName = "",
                sid = 1,
                photo = "",
                halfdayDTL = null,
                forwardedBy = 0,
                forwardedByName = null,
                cancelby = null,
                cancelledOn = null,
                showCancelButton = false,
                attPer = "85.5",
                isDirector = false
            ),
            applType = ApplType.STUDENT,
            selectedTab = LeaveReportTab.PENDING,
            isSelected = true,
            showCheckbox = true,
            showAttendancePercentage = true,
            onCheckboxChanged = {},
            onApproveClicked = {},
            onRejectClicked = {},
            onForwardClicked = {},
            onCancelClicked = {}
        )
    }
}
