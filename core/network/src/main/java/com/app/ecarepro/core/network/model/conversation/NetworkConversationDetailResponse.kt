package com.app.ecarepro.core.network.model.conversation

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkConversationDetailResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("subject") val subject: String? = null,
    @SerialName("msgID") val msgID: Int? = null,
    @SerialName("readCount") val readCount: Int? = null,
    @SerialName("canReply") val canReply: Boolean? = null,
    @SerialName("receiverType") val receiverType: Int? = null,
    @SerialName("receiverID") val receiverID: Int? = null,
    @SerialName("senderDTL") val senderDTL: NetworkConversationSender? = null,
    @SerialName("recipients") val recipients: List<NetworkConversationRecipient>? = null,
    @SerialName("msgDTL") val msgDTL: List<NetworkConversationMessage>? = null,
) : NetworkResponse

@Serializable
data class NetworkConversationMessage(
    @SerialName("msgType") val msgType: Int? = null,
    @SerialName("sentOn") val sentOn: String? = null,
    @SerialName("isMine") val isMine: Boolean? = null,
    @SerialName("rplID") val rplID: Int? = null,
    @SerialName("msgID") val msgID: Int? = null,
    @SerialName("senderDTL") val senderDTL: NetworkConversationSender? = null,
    @SerialName("body") val body: String? = null,
    @SerialName("filePath") val filePath: String? = null,
    @SerialName("filePaths") val filePaths: List<String>? = null,
)
