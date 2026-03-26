package com.app.ecarepro.feature.leave.appliedleaves.data

import kotlinx.serialization.Serializable

enum class LeaveAction(val value: Int) {
    APPROVE(1),
    REJECT(2),
    FORWARD(3),
    CANCEL(4);

    val displayText: String
        get() = when (this) {
            APPROVE -> "approved"
            REJECT -> "rejected"
            FORWARD -> "forwarded"
            CANCEL -> "cancelled"
        }
}

@Serializable
data class LeaveActionRequest(
    val action: Int,
    val lvID: Int,
    val lvIDs: String? = null,
    val applType: Int = 1,
    val forwardedTo: Int = 0,
    val isPartialApproved: Boolean = false,
    val partialFromDate: String = "",
    val partialTillDate: String = "",
    val rejectionReason: String? = null
)

@Serializable
data class GenericResponse(
    val status: String? = null,
    val errorCode: Int = 0,
    val message: String? = null
)
