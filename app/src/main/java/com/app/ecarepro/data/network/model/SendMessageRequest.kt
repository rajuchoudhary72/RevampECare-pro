package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class SendMessageRequest(
    @SerializedName("attachment")
    val attachment: Attachment?,
    @SerializedName("body")
    val body: String?,
    @SerializedName("classIDs")
    val classIDs: String?,
    @SerializedName("device")
    val device: Int?,
    @SerializedName("geoCoordinate")
    val geoCoordinate: String?,
    @SerializedName("ipAddress")
    val ipAddress: String?,
    @SerializedName("msgType")
    val msgType: Int?,
    @SerializedName("multipleAttachments")
    val multipleAttachments: List<String>?,
    @SerializedName("recipient")
    val recipient: List<Recipients>?,
    @SerializedName("recipientType")
    val recipientType: Int?,
    @SerializedName("subject")
    val subject: String?,
    @SerializedName("scholarType")
    val scholarType: Int
)

data class Attachment(
    @SerializedName("attachment")
    val attachment: String?,
    @SerializedName("fileExt")
    val fileExt: String?,
    @SerializedName("fileURL")
    val fileURL: String?
)

data class Recipients(
    @SerializedName("receiverID")
    val receiverID: String?,
    @SerializedName("receiverType")
    val receiverType: Int?
)
