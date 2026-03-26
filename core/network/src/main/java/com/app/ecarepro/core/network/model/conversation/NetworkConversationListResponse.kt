package com.app.ecarepro.core.network.model.conversation

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkConversationListResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("canDeleteConv") val canDeleteConv: Boolean? = null,
    @SerialName("conversation") val conversation: List<NetworkConversationItem>? = null,
    @SerialName("total") val total: Int? = null,
) : NetworkResponse

@Serializable
data class NetworkConversationItem(
    @SerialName("msgType") val msgType: Int? = null,
    @SerialName("abbreviation") val abbreviation: String? = null,
    @SerialName("sentOn") val sentOn: String? = null,
    @SerialName("readCount") val readCount: Int? = null,
    @SerialName("msgID") val msgID: String? = null,
    @SerialName("subject") val subject: String? = null,
    @SerialName("isReplyMsg") val isReplyMsg: Int? = null,
    @SerialName("senderDTL") val senderDTL: NetworkConversationSender? = null,
    @SerialName("recipients") val recipients: List<NetworkConversationRecipient>? = null,
)

@Serializable
data class NetworkConversationSender(
    @SerialName("sndrId") val sndrId: String? = null,
    @SerialName("childName") val childName: String? = null,
    @SerialName("className") val className: String? = null,
    @SerialName("senderID") val senderID: Int? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("senderType") val senderType: Int? = null,
    @SerialName("name") val name: String? = null,
)

@Serializable
data class NetworkConversationRecipient(
    @SerialName("photo") val photo: String? = null,
    @SerialName("hasRead") val hasRead: Boolean? = null,
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("className") val className: String? = null,
    @SerialName("receiverType") val receiverType: Int? = null,
    @SerialName("admissionNo") val admissionNo: String? = null,
    @SerialName("childPhoto") val childPhoto: String? = null,
    @SerialName("isSelect") val isSelect: Boolean? = null,
    @SerialName("readAt") val readAt: String? = null,
    @SerialName("rollNumber") val rollNumber: String? = null,
    @SerialName("receiverID") val receiverID: Int? = null,
    @SerialName("rcvID") val rcvID: String? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("childName") val childName: String? = null,
)
