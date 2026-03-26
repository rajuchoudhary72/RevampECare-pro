package com.app.ecarepro.feature.leave.appliedleaves.data

data class LeaveCardPresentation(
    val id: String,
    val lvID: Int,
    val displayName: String,
    val displaySubtitle: String,
    val photoURL: String?,
    val appliedDate: String,
    val leaveType: String?,
    val attendanceText: String?,
    val applicantName: String?,
    val fromDate: String,
    val toDate: String,
    val durationText: String,
    val reason: String,
    val status: LeaveStatus,
    val statusText: String,
    val actionOnText: String?,
    val actionByName: String?,
    val hasAttachment: Boolean,
    val attachmentCount: Int,
    val attachmentURL: String?,
    val isSelected: Boolean,
    val screenType: AppliedLeavesScreenType,
    val showCancelButton: Boolean = false
) {
    val showCheckbox: Boolean = screenType.showCheckbox && status == LeaveStatus.PENDING
    val showActionButtons: Boolean = screenType.canTakeAction && status == LeaveStatus.PENDING
    val showForwardButton: Boolean = screenType.showForwardButton && status == LeaveStatus.PENDING
    val showStatusBadge: Boolean = !screenType.canTakeAction || status != LeaveStatus.PENDING
    val showApplicantName: Boolean = screenType == AppliedLeavesScreenType.STUDENT_LEAVES && applicantName != null
    val showAttendance: Boolean = screenType == AppliedLeavesScreenType.STUDENT_LEAVES && attendanceText != null
    val showLeaveType: Boolean = !leaveType.isNullOrEmpty()

    val statusBadgeText: String
        get() = when (status) {
            LeaveStatus.APPROVED -> {
                if (actionOnText != null && actionByName != null) {
                    "Approved on $actionOnText • $actionByName"
                } else "Approved"
            }
            LeaveStatus.REJECTED -> {
                if (actionOnText != null && actionByName != null) {
                    "Rejected on $actionOnText • $actionByName"
                } else "Rejected"
            }
            LeaveStatus.PENDING -> "Pending"
            LeaveStatus.CANCELLED -> "Cancelled"
            LeaveStatus.UNKNOWN -> ""
        }

    companion object {
        fun fromLeave(
            leave: Leave,
            isSelected: Boolean = false,
            screenType: AppliedLeavesScreenType,
            showAttendance: Boolean = false
        ): LeaveCardPresentation {
            val isStaff = screenType == AppliedLeavesScreenType.STAFF_LEAVES
            val durationValue = leave.durationText(isStaff)
            val durationFormatted = durationValue.toDoubleOrNull()?.toInt()?.let { days ->
                "$days day${if (days == 1) "" else "s"}"
            } ?: durationValue

            return LeaveCardPresentation(
                id = leave.id,
                lvID = leave.lvID ?: 0,
                displayName = leave.displayName(screenType),
                displaySubtitle = leave.displaySubtitle(screenType),
                photoURL = leave.displayPhoto(screenType),
                appliedDate = "Applied: ${leave.submittedOn ?: "N/A"}",
                leaveType = leave.leaveType,
                attendanceText = if (showAttendance && leave.attPer != null) {
                    "Attendance: ${leave.attPer}%"
                } else null,
                applicantName = leave.applicantName?.let { "Applicant: $it" },
                fromDate = leave.fromDate ?: "",
                toDate = leave.tillDate ?: "",
                durationText = durationFormatted,
                reason = leave.reason?.takeIf { it.isNotEmpty() } ?: "No reason provided",
                status = leave.leaveStatus,
                statusText = leave.leaveStatus.displayText,
                actionOnText = leave.actionOn,
                actionByName = leave.teacherName,
                hasAttachment = leave.attachmentCount > 0,
                attachmentCount = leave.attachmentCount,
                attachmentURL = leave.attachment,
                isSelected = isSelected,
                screenType = screenType,
                showCancelButton = leave.showCancelButton == true
            )
        }
    }
}
