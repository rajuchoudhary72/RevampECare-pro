package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class InboxMessageDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("sender")
    val sender: List<InboxMessage>?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("total")
    val total: Int?
)

data class InboxMessage(
    @SerializedName("childName")
    val childName: String?,
    @SerializedName("className")
    val className: String?,
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("senderID")
    val senderID: Int?,
    @SerializedName("senderType")
    val senderType: Int?,
    @SerializedName("sentOn")
    val sentOn: String,
    @SerializedName("unread")
    val unread: Int?
)