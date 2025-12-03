package com.app.ecarepro.core.network.model.leave

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkLeaveActionRequest(
    @SerialName("applType")
    val applType: Int,
    @SerialName("lvID")
    val lvID: Int,
    @SerialName("action")
    val action: Int,
    @SerialName("forwardedTo")
    val forwardedTo: Int = 0,
    @SerialName("rejectionReason")
    val rejectionReason: String? = null,
    @SerialName("isPartialApproved")
    val isPartialApproved: Boolean = false,
    @SerialName("partialFromDate")
    val partialFromDate: String? = null,
    @SerialName("partialTillDate")
    val partialTillDate: String? = null
)
