package com.app.ecarepro.core.network.model.leave

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
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
    val isRejectionReasonReq: Boolean,
    @SerialName("minDate")
    val minDate: String? = null,
    @SerialName("dtl")
    val dtl: List<NetworkLeaveReportItem>,
    @SerialName("myReporting")
    val myReporting: List<NetworkReportingManager>? = null
)

@InternalSerializationApi
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
    @SerialName("duration")
    val duration: String,
    @SerialName("reason")
    val reason: String,
    @SerialName("applicantName")
    val applicantName: String,
    @SerialName("applicantPhoto")
    val applicantPhoto: String?,
    @SerialName("studentName")
    val studentName: String,
    @SerialName("studentPhoto")
    val studentPhoto: String?,
    @SerialName("studentClass")
    val studentClass: String,
    @SerialName("admissionNumber")
    val admissionNumber: String? = null,
    @SerialName("classteacherName")
    val classTeacherName: String? = null,
    @SerialName("status")
    val status: String,
    @SerialName("actionOn")
    val actionOn: String?,
    @SerialName("sid")
    val sid: Int,
    @SerialName("teacherName")
    val teacherName: String?,
    @SerialName("designation")
    val designation: String?,
    @SerialName("photo")
    val photo: String?,
    @SerialName("attachment")
    val attachment: String?,
    @SerialName("rejectionReason")
    val rejectionReason: String?,
    @SerialName("isPartialApproved")
    val isPartialApproved: Boolean = false,
    @SerialName("partialFromDate")
    val partialFromDate: String?,
    @SerialName("partialTillDate")
    val partialTillDate: String?,
    @SerialName("partialDuration")
    val partialDuration: String?,
    @SerialName("attPer")
    val attPer: String?,
    @SerialName("isSelected")
    val isSelected: Boolean = false
)

@InternalSerializationApi
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
