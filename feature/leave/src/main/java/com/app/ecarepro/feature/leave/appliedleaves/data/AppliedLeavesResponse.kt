package com.app.ecarepro.feature.leave.appliedleaves.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppliedLeavesResponse(
    val status: String? = null,
    val errorCode: Int = 0,
    val message: String? = null,
    @SerialName("canTalkeAction") val canTakeAction: Boolean? = null,
    val isRejectionReasonReq: Boolean? = null,
    val minDate: String? = null,
    val dtl: List<Leave>? = null
) {
    val leaves: List<Leave> get() = dtl ?: emptyList()
}
