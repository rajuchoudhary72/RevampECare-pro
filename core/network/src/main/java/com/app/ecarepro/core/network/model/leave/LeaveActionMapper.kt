package com.app.ecarepro.core.network.model.leave

import com.app.ecarepro.core.domain.model.LeaveActionRequest
import com.app.ecarepro.core.domain.model.LeaveActionResponse

fun LeaveActionRequest.toNetworkModel(): NetworkLeaveActionRequest {
    return NetworkLeaveActionRequest(
        applType = applType,
        lvID = lvID,
        action = action.value,
        forwardedTo = forwardedTo,
        rejectionReason = rejectionReason,
        isPartialApproved = isPartialApproved,
        partialFromDate = partialFromDate,
        partialTillDate = partialTillDate
    )
}

fun NetworkLeaveActionResponse.toDomainModel(): LeaveActionResponse {
    return LeaveActionResponse(
        message = message
    )
}
