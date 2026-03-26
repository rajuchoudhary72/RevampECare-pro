package com.app.ecarepro.feature.leave.appliedleaves.data

enum class AppliedLeavesScreenType {
    SELF_LEAVES,      // Student/Staff viewing own leaves
    STUDENT_LEAVES,   // Teacher/Management viewing student leaves
    STAFF_LEAVES;     // Management viewing staff leaves

    val title: String
        get() = when (this) {
            SELF_LEAVES -> "My Leaves"
            STUDENT_LEAVES, STAFF_LEAVES -> "Applied Leaves"
        }

    val showTabs: Boolean get() = this != SELF_LEAVES
    val showApplyButton: Boolean get() = this == SELF_LEAVES
    val showAttendanceToggle: Boolean get() = this == STUDENT_LEAVES
    val showForwardButton: Boolean get() = this == STAFF_LEAVES
    val canTakeAction: Boolean get() = this != SELF_LEAVES
    val showCheckbox: Boolean get() = this != SELF_LEAVES

    val availableTabs: List<LeaveStatus>
        get() = when (this) {
            SELF_LEAVES -> emptyList()
            STUDENT_LEAVES, STAFF_LEAVES -> listOf(
                LeaveStatus.PENDING,
                LeaveStatus.APPROVED,
                LeaveStatus.REJECTED,
                LeaveStatus.CANCELLED
            )
        }
}
