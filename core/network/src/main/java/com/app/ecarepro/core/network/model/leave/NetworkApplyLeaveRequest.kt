package com.app.ecarepro.core.network.model.leave

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkApplyLeaveRequest(
    @SerialName("leaveID")
    val leaveID: Int,
    @SerialName("fromDate")
    val fromDate: String,
    @SerialName("tillDate")
    val tillDate: String,
    @SerialName("duration")
    val duration: Double,
    @SerialName("reason")
    val reason: String,
    @SerialName("attachment")
    val attachment: String? = null,
    @SerialName("attachmentExt")
    val attachmentExt: String? = null
)

@Serializable
data class NetworkApplyLeaveResponse(
    @SerialName("errorCode")
    val errorCode: Int,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String
)
