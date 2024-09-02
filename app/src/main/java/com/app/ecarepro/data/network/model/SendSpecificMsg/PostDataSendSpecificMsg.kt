package com.app.ecarepro.data.network.model.SendSpecificMsg

data class PostDataSendSpecificMsg(
    val attachment: Attachment?,
    val device: Int,
    val ipAddress: String,
    val msgType: Int,
    val recipient: List<Recipient>,
    val subject: String
)