package com.app.ecarepro.core.domain.model.smsreport

data class SMSTypeItem(
    val typeID: Int,
    val subject: String,
)

data class SMSReportItem(
    val receiverName: String,
    val receiverDesignation: String,
    val receiverPhotoUrl: String?,
    val messageText: String,
    val smsTypeName: String,
    val status: String,
    val isSent: Boolean,
    val senderName: String,
    val senderPhotoUrl: String?,
    val sentOnDate: String,
)
