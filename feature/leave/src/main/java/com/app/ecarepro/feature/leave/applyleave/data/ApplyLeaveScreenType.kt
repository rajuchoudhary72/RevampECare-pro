package com.app.ecarepro.feature.leave.applyleave.data

enum class ApplyLeaveScreenType {
    STAFF,
    STUDENT;

    val showLeaveTypeSelector: Boolean get() = this == STAFF
    val showLeaveBalance: Boolean get() = this == STAFF
    val showSessionSelector: Boolean get() = this == STAFF
}

enum class SessionType(val displayName: String, val value: Double) {
    FIRST_HALF("First half", 0.5),
    SECOND_HALF("Second half", 0.5)
}

enum class LeaveReason(val displayName: String) {
    MEDICAL("Medical"),
    PERSONAL("Personal"),
    FAMILY_EMERGENCY("Family Emergency"),
    VACATION("Vacation"),
    OTHER("Other")
}
