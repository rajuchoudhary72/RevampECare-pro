package com.app.ecarepro.core.domain.model

data class ApplyLeaveRequest(
    val leaveID: Int,
    val fromDate: String,
    val tillDate: String,
    val duration: Double,
    val reason: String,
    val attachment: String? = null,
    val attachmentExt: String? = null
)

data class ApplyLeaveResponse(
    val errorCode: Int,
    val status: String,
    val message: String
) {
    val isSuccess: Boolean
        get() = errorCode == 0 && status.equals("ok", ignoreCase = true)
}
