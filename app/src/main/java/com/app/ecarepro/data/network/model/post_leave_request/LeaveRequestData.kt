package com.app.ecarepro.data.network.model.post_leave_request

data class LeaveRequestData(
    val duration: Double,
    val fileAttachment: FileAttachment?,
    val fromDate: String,
    val halfdayDTL: List<HalfdayDTL>,
    val leaveID: Int,
    val reason: String,
    val tillDate: String
)