package com.app.ecarepro.core.network.model.leave

import com.app.ecarepro.core.domain.model.LeaveReportItem
import com.app.ecarepro.core.domain.model.LeaveReportResponse
import com.app.ecarepro.core.domain.model.ReportingManager
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
fun NetworkLeaveReportResponse.toDomainModel(): LeaveReportResponse {
    return LeaveReportResponse(
        errorCode = errorCode,
        status = status,
        message = message,
        total = dtl.size,
        dtl = dtl.map { it.toDomainModel() },
        myReporting = myReporting?.map { it.toDomainModel() } ?: emptyList(),
        isRejectionReasonReq = isRejectionReasonReq
    )
}

@OptIn(InternalSerializationApi::class)
fun NetworkLeaveReportItem.toDomainModel(): LeaveReportItem {
    return LeaveReportItem(
        lvID = lvID,
        fromDate = fromDate,
        tillDate = tillDate,
        duration = duration.toDoubleOrNull() ?: 0.0,
        durationStr = duration,
        reason = reason,
        leaveType = "", // Will be populated from network if available
        leaveAbbr = "", // Will be populated from network if available
        status = status,
        submittedOn = submittedOn,
        actionOn = actionOn ?: "",
        rejectionReason = rejectionReason ?: "",
        attachment = attachment ?: "",
        applicantName = applicantName,
        applicantPhoto = applicantPhoto ?: "",
        studentName = studentName,
        studentPhoto = studentPhoto ?: "",
        studentClass = studentClass,
        designation = designation ?: "",
        teacherID = 0, // Will be populated if needed
        teacherName = teacherName ?: "",
        sid = sid,
        photo = photo ?: "",
        halfdayDTL = null,
        forwardedBy = 0,
        forwardedByName = null,
        cancelby = null,
        cancelledOn = null,
        showCancelButton = status == "Approved",
        attPer = attPer ?: "",
        isDirector = false
    )
}

@OptIn(InternalSerializationApi::class)
fun NetworkReportingManager.toDomainModel(): ReportingManager {
    return ReportingManager(
        teacherID = teacherID,
        teacherName = teacherName,
        photo = photo,
        designation = designation
    )
}
