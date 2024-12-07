package com.app.ecarepro.data.network.model

data class PostLeaveAction(
    val action: Int,
    val applType: Int,
    val forwardedTo: Int,
    val lvID: Int?,
    val lvIDs: String?,
    val rejectionReason: String,
    val isPartialApproved: Boolean?,
    val partialFromDate: String?,
    val partialTillDate: String?,
)