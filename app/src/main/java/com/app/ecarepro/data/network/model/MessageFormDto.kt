package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class MessageFormDto(
    @SerializedName("allMessages")
    val allMessages: List<Conversation>?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("senderDTL")
    val senderDTL: Sender,
    @SerializedName("status")
    val status: String?,
    @SerializedName("total")
    val total: Int
)

data class Conversation(
    @SerializedName("abbreviation")
    val abbreviation: String?,
    @SerializedName("hasRead")
    val hasRead: Boolean?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("msgID")
    val msgID: Int?,
    @SerializedName("msgType")
    val msgType: Int?,
    @SerializedName("sentOn")
    val sentOn: String?,
    @SerializedName("subject")
    val subject: String?
)

data class Sender(
    @SerializedName("childName")
    val childName: Any?,
    @SerializedName("className")
    val className: Any?,
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("id")
    val id: Any?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("senderID")
    val senderID: Int?,
    @SerializedName("senderType")
    val senderType: Int?,
    @SerializedName("sentOn")
    val sentOn: Any?,
    @SerializedName("unread")
    val unread: Int?
)