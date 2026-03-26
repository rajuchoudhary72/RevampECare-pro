package com.app.ecarepro.feature.leave.appliedleaves.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.util.UUID

@Serializable
data class Leave(
    val lvID: Int? = null,
    val fromDate: String? = null,
    val tillDate: String? = null,
    val submittedOn: String? = null,
    val duration: JsonElement? = null,  // Can be Int or String
    @SerialName("duration_str") val durationStr: String? = null,
    val reason: String? = null,
    val applicantName: String? = null,
    val applicantPhoto: String? = null,
    val studentName: String? = null,
    val studentPhoto: String? = null,
    val studentClass: String? = null,
    val admissionNumber: String? = null,
    val classteacherName: String? = null,
    val status: String? = null,
    val actionOn: String? = null,
    val sid: Int? = null,
    val teacherName: String? = null,
    val designation: String? = null,
    val photo: String? = null,
    val attachment: String? = null,
    val rejectionReason: String? = null,
    val isPartialApproved: Boolean? = null,
    val partialFromDate: String? = null,
    val partialTillDate: String? = null,
    val partialDuration: String? = null,
    val attPer: String? = null,
    val isSelected: Boolean? = null,
    val leaveType: String? = null,
    val leaveAbbr: String? = null,
    val forwardedBy: Int? = null,
    val forwardedByName: String? = null,
    val showCancelButton: Boolean? = null
) {
    val id: String get() = lvID?.toString() ?: UUID.randomUUID().toString()

    val leaveStatus: LeaveStatus
        get() = status?.let { LeaveStatus.fromString(it) } ?: LeaveStatus.UNKNOWN

    fun displayName(screenType: AppliedLeavesScreenType): String = when (screenType) {
        AppliedLeavesScreenType.SELF_LEAVES -> studentName ?: teacherName ?: ""
        AppliedLeavesScreenType.STUDENT_LEAVES -> studentName ?: ""
        AppliedLeavesScreenType.STAFF_LEAVES -> teacherName ?: studentName ?: ""
    }

    fun displaySubtitle(screenType: AppliedLeavesScreenType): String = when (screenType) {
        AppliedLeavesScreenType.SELF_LEAVES,
        AppliedLeavesScreenType.STUDENT_LEAVES -> studentClass ?: ""
        AppliedLeavesScreenType.STAFF_LEAVES -> designation ?: ""
    }

    fun displayPhoto(screenType: AppliedLeavesScreenType): String? = when (screenType) {
        AppliedLeavesScreenType.SELF_LEAVES -> studentPhoto ?: photo
        AppliedLeavesScreenType.STUDENT_LEAVES -> studentPhoto
        AppliedLeavesScreenType.STAFF_LEAVES -> photo ?: studentPhoto
    }

    fun durationText(isStaff: Boolean): String {
        if (isStaff && durationStr != null) return durationStr

        return try {
            duration?.toString()?.replace("\"", "") ?: "N/A"
        } catch (e: Exception) {
            "N/A"
        }
    }

    val attachmentCount: Int
        get() = attachment?.split(",")?.size ?: 0

    fun withUpdatedStatus(newStatus: LeaveStatus): Leave = copy(status = newStatus.value)
}
