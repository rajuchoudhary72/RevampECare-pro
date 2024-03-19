package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class SentMessageDto(
    @SerializedName("canDelete")
    val canDelete: Boolean?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("sentMessages")
    val sentMessages: List<SentMessage>?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("total")
    val total: Int
)

data class SentMessage(
    @SerializedName("abbreviation")
    val abbreviation: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isReplyMsg")
    val isReplyMsg: Boolean?,
    @SerializedName("msgID")
    val msgID: Int?,
    @SerializedName("msgType")
    val msgType: Int?,
    @SerializedName("recipients")
    val recipients: List<Recipient>?,
    @SerializedName("sentOn")
    val sentOn: String,
    @SerializedName("subject")
    val subject: String?
)


data class Recipient(
    @SerializedName("admissionNo")
    val admissionNo: Any?,
    @SerializedName("childName")
    val childName: String?,
    @SerializedName("childPhoto")
    val childPhoto: Any?,
    @SerializedName("className")
    val className: String?,
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("hasRead")
    val hasRead: Boolean?,
    @SerializedName("isSelect")
    val isSelect: Boolean?,
    @SerializedName("mobile")
    val mobile: Any?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("readAt")
    val readAt: Any?,
    @SerializedName("receiverID")
    val receiverID: Int?,
    @SerializedName("receiverType")
    val receiverType: Int?,
    @SerializedName("rollNumber")
    val rollNumber: Any?
) : Serializable

data class RecipientDto(
    val recipients: List<Recipient>
) : Serializable