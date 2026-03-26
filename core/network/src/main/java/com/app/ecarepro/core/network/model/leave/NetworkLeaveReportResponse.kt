package com.app.ecarepro.core.network.model.leave

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkLeaveReportResponse(
    @SerialName("errorCode")
    val errorCode: Int,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("canTalkeAction")
    val canTakeAction: Boolean = true,
    @SerialName("isRejectionReasonReq")
    val isRejectionReasonReq: Boolean = false,
    @SerialName("minDate")
    val minDate: String? = null,
    @SerialName("dtl")
    val dtl: List<NetworkLeaveReportItem>? = null,
    @SerialName("myReporting")
    val myReporting: List<NetworkReportingManager>? = null
)

@Serializable
data class NetworkLeaveReportItem(
    @SerialName("lvID")
    val lvID: Int,
    @SerialName("fromDate")
    val fromDate: String,
    @SerialName("tillDate")
    val tillDate: String,
    @SerialName("submittedOn")
    val submittedOn: String,
    @SerialName("leaveType")
    val leaveType: String? = null,
    @SerialName("leaveAbbr")
    val leaveAbbr: String? = null,
    @SerialName("duration")
    val duration: Double,
    @SerialName("duration_str")
    val durationStr: String? = null,
    @SerialName("halfdayDTL")
    val halfdayDTL: List<NetworkHalfDayDetail>? = null,
    @SerialName("reason")
    val reason: String,
    @SerialName("teacherID")
    val teacherID: Int? = null,
    @SerialName("applicantName")
    val applicantName: String,
    @SerialName("applicantPhoto")
    val applicantPhoto: String?,
    @SerialName("studentName")
    val studentName: String? = null,
    @SerialName("studentPhoto")
    val studentPhoto: String? = null,
    @SerialName("studentClass")
    val studentClass: String? = null,
    @SerialName("admissionNumber")
    val admissionNumber: String? = null,
    @SerialName("classteacherName")
    val classTeacherName: String? = null,
    @SerialName("status")
    val status: String,
    @SerialName("actionOn")
    val actionOn: String? = null,
    @SerialName("sid")
    val sid: Int = 0,
    @SerialName("teacherName")
    val teacherName: String? = null,
    @SerialName("designation")
    val designation: String? = null,
    @SerialName("photo")
    val photo: String? = null,
    @SerialName("attachment")
    val attachment: String? = null,
    @SerialName("rejectionReason")
    val rejectionReason: String? = null,
    @SerialName("isPartialApproved")
    val isPartialApproved: Boolean = false,
    @SerialName("partialFromDate")
    val partialFromDate: String? = null,
    @SerialName("partialTillDate")
    val partialTillDate: String? = null,
    @SerialName("partialDuration")
    val partialDuration: String? = null,
    @SerialName("attPer")
    val attPer: String? = null,
    @SerialName("isSelected")
    val isSelected: Boolean = false,
    @SerialName("forwardedBy")
    val forwardedBy: Int = 0,
    @SerialName("forwardedByName")
    val forwardedByName: String? = null,
    @SerialName("showCancelButton")
    val showCancelButton: Boolean = false,
    @SerialName("cancelby")
    val cancelBy: String? = null,
    @SerialName("cancelledOn")
    val cancelledOn: String? = null
)

@Serializable
data class NetworkHalfDayDetail(
    @SerialName("halfdayType")
    val halfdayType: Int,
    @SerialName("halfDayOn")
    val halfDayOn: String
)

@Serializable
data class NetworkReportingManager(
    @SerialName("teacherID")
    val teacherID: Int,
    @SerialName("teacherName")
    val teacherName: String,
    @SerialName("photo")
    val photo: String? = null,
    @SerialName("designation")
    val designation: String? = null
)
