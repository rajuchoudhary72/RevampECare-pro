package com.app.ecarepro.core.domain.model

data class LeaveActionRequest(
    val applType: Int,
    val lvID: Int,
    val action: LeaveAction,
    val forwardedTo: Int = 0,
    val rejectionReason: String? = null,
    val isPartialApproved: Boolean = false,
    val partialFromDate: String? = null,
    val partialTillDate: String? = null
)

data class LeaveActionResponse(
    val message: String
)

enum class LeaveAction(val value: Int) {
    APPROVE(1),
    REJECT(2),
    CANCEL(3),
    FORWARD(4)
}
