package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class ConversationDetailsDto(
    @SerializedName("canReply")
    val canReply: Boolean?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("msgDTL")
    val msgDTL: List<Message>?,
    @SerializedName("recipients")
    val recipients: List<Recipient>?,
    @SerializedName("msgID")
    val msgID: Int?,
    @SerializedName("readCount")
    val readCount: Int?,
    @SerializedName("receiverID")
    val receiverID: Int?,
    @SerializedName("receiverType")
    val receiverType: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("subject")
    val subject: String?
)

data class Message(
    @SerializedName("body")
    val body: String?,
    @SerializedName("filePath")
    val filePath: Any?,
    @SerializedName("filePaths")
    val filePaths: List<String>?,
    @SerializedName("msgID")
    val msgID: Int?,
    @SerializedName("msgType")
    val msgType: Int?,
    @SerializedName("rplID")
    val rplID: Int?,
    @SerializedName("senderDTL")
    val senderDTL: Sender,
    @SerializedName("sentOn")
    val sentOn: String?,
    @SerializedName("isMine")
    val isMine: Boolean
)