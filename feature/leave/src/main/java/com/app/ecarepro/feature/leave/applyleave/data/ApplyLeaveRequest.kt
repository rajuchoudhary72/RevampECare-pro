package com.app.ecarepro.feature.leave.applyleave.data

import kotlinx.serialization.Serializable

@Serializable
data class ApplyLeaveRequest(
    val duration: Double,
    val fileAttachment: FileAttachment? = null,
    val fromDate: String,
    val leaveID: Int,
    val reason: String,
    val tillDate: String
)

@Serializable
data class FileAttachment(
    val attachment: String,  // Base64 encoded
    val fileExt: String,
    val fileURL: String
)

@Serializable
data class ApplyLeaveResponse(
    val errorCode: Int = 0,
    val status: String? = null,
    val message: String? = null
)
