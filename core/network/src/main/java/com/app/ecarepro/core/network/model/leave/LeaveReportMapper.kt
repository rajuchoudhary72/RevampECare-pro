package com.app.ecarepro.core.network.model.leave

import com.app.ecarepro.core.domain.model.ApplyLeaveRequest
import com.app.ecarepro.core.domain.model.ApplyLeaveResponse
import com.app.ecarepro.core.domain.model.LeaveReportItem
import com.app.ecarepro.core.domain.model.LeaveReportResponse
import com.app.ecarepro.core.domain.model.ReportingManager

fun NetworkLeaveReportResponse.toDomainModel(): LeaveReportResponse {
    val leaveItems = dtl ?: emptyList()
    return LeaveReportResponse(
        errorCode = errorCode,
        status = status,
        message = message,
        total = leaveItems.size,
        dtl = leaveItems.map { it.toDomainModel() },
        myReporting = myReporting?.map { it.toDomainModel() } ?: emptyList(),
        isRejectionReasonReq = isRejectionReasonReq
    )
}

fun NetworkLeaveReportItem.toDomainModel(): LeaveReportItem {
    return LeaveReportItem(
        lvID = lvID,
        fromDate = fromDate,
        tillDate = tillDate,
        duration = duration,
        durationStr = durationStr ?: duration.toString(),
        reason = reason,
        leaveType = leaveType ?: "",
        leaveAbbr = leaveAbbr ?: "",
        status = status,
        submittedOn = submittedOn,
        actionOn = actionOn ?: "",
        rejectionReason = rejectionReason ?: "",
        attachment = attachment ?: "",
        applicantName = applicantName,
        applicantPhoto = applicantPhoto ?: "",
        studentName = studentName ?: "",
        studentPhoto = studentPhoto ?: "",
        studentClass = studentClass ?: "",
        designation = designation ?: "",
        teacherID = teacherID ?: 0,
        teacherName = teacherName ?: "",
        sid = sid,
        photo = photo ?: "",
        halfdayDTL = halfdayDTL,
        forwardedBy = forwardedBy,
        forwardedByName = forwardedByName,
        cancelby = cancelBy,
        cancelledOn = cancelledOn,
        showCancelButton = showCancelButton,
        attPer = attPer ?: "",
        isDirector = false
    )
}

fun NetworkReportingManager.toDomainModel(): ReportingManager {
    return ReportingManager(
        teacherID = teacherID,
        teacherName = teacherName,
        photo = photo,
        designation = designation
    )
}

// Apply Leave Mappers
fun ApplyLeaveRequest.toNetworkModel(): NetworkApplyLeaveRequest {
    return NetworkApplyLeaveRequest(
        leaveID = leaveID,
        fromDate = fromDate,
        tillDate = tillDate,
        duration = duration,
        reason = reason,
        attachment = attachment,
        attachmentExt = attachmentExt
    )
}

fun NetworkApplyLeaveResponse.toDomainModel(): ApplyLeaveResponse {
    return ApplyLeaveResponse(
        errorCode = errorCode,
        status = status,
        message = message
    )
}
