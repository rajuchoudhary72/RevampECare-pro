package com.app.ecarepro.core.domain.model

import javax.annotation.concurrent.Immutable

@Immutable
data class LeaveApplication(
    val lvID: Int,
    val fromDate: String,
    val tillDate: String,
    val duration: Double,
    val durationStr: String,
    val reason: String,
    val leaveType: String,
    val leaveAbbr: String,
    val status: String,
    val submittedOn: String,
    val actionOn: String,
    val rejectionReason: String,
    val attachment: String,
    val applicantName: String,
    val applicantPhoto: String,
    val studentName: String,
    val studentPhoto: String,
    val studentClass: String,
    val designation: String,
    val teacherID: Int,
    val teacherName: String,
    val sid: Int,
    val photo: String,
    val halfdayDTL: Any?,
    val forwardedBy: Int,
    val forwardedByName: Any?,
    val cancelby: String?,
    val cancelledOn: String?,
    val showCancelButton: Boolean,
    val attPer: String
)

@Immutable
data class LeaveType(
    val leaveID: Int,
    val leaveType: String,
    val leaveAbbr: String,
    val total: Double,
    val taken: Double,
    val available: Double,
    val minAcceptableLimit: Double,
    val maxAcceptableLimit: Double,
    val applyBeforeHours: Int,
    val attachmentMandatory: Boolean,
    val sandwichEnable: Boolean,
    val minimumLimit: Int,
    val inCurMonth: Double
)

@Immutable
data class LeaveSettings(
    val leaveTypes: List<LeaveType>,
    val maxDaysInAdvance: Int,
    val allowHalfDay: Boolean,
    val allowAttachment: Boolean
)

@Immutable
data class LeaveListResponse(
    val errorCode: Int,
    val status: String,
    val message: String,
    val total: Int,
    val leaves: List<LeaveApplication>
)

enum class LeaveStatus(val displayName: String, val colorHex: String) {
    PENDING("Pending", "#FFA500"),     // Orange
    APPROVED("Approved", "#4CAF50"),   // Green
    REJECTED("Rejected", "#F44336"),   // Red
    CANCELLED("Cancelled", "#9E9E9E")  // Grey
}

enum class HalfDayType {
    FIRST_HALF,
    SECOND_HALF,
    NONE
}

@Immutable
data class LeaveBalance(
    val leaveType: String,
    val total: Double,
    val taken: Double,
    val available: Double
)

enum class ApplType(val value: Int) {
    STUDENT(1),
    STAFF(3)
}

enum class LeaveReportTab(val displayName: String, val statusValue: Int) {
    PENDING("Pending", 0),
    APPROVED("Approved", 1),
    REJECTED("Rejected", 2),
    CANCELLED("Cancelled", 3)
}

@Immutable
data class ReportingManager(
    val teacherID: Int,
    val teacherName: String,
    val photo: String? = null,
    val designation: String? = null
)

@Immutable
data class LeaveReportResponse(
    val errorCode: Int,
    val status: String,
    val message: String,
    val total: Int,
    val dtl: List<LeaveReportItem>,
    val myReporting: List<ReportingManager>,
    val isRejectionReasonReq: Boolean
)

@Immutable
data class LeaveReportItem(
    val lvID: Int,
    val fromDate: String,
    val tillDate: String,
    val duration: Double,
    val durationStr: String,
    val reason: String,
    val leaveType: String,
    val leaveAbbr: String,
    val status: String,
    val submittedOn: String,
    val actionOn: String,
    val rejectionReason: String,
    val attachment: String,
    val applicantName: String,
    val applicantPhoto: String,
    val studentName: String,
    val studentPhoto: String,
    val studentClass: String,
    val designation: String,
    val teacherID: Int,
    val teacherName: String,
    val sid: Int,
    val photo: String,
    val halfdayDTL: Any?,
    val forwardedBy: Int,
    val forwardedByName: String?,
    val cancelby: String?,
    val cancelledOn: String?,
    val showCancelButton: Boolean,
    val attPer: String,
    val isDirector: Boolean = false
)
