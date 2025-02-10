package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class ReplyMessageRequestDto(
    @SerializedName("body")
    val body: String?,
    @SerializedName("device")
    val device: Int = 1,
    @SerializedName("ipAddress")
    val ipAddress: String?,
    @SerializedName("msgID")
    val msgID: Int?,
    @SerializedName("msgType")
    val msgType: Int?,
    @SerializedName("receiverID")
    val receiverID: Int?,
    @SerializedName("receiverType")
    val receiverType: Int?,
    @SerializedName("attachment")
    val attachment: Attachment?
)