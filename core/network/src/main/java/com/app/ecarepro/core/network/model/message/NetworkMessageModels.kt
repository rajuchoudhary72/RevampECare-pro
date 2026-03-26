package com.app.ecarepro.core.network.model.message

import com.app.ecarepro.core.domain.model.message.ConversationMessage
import com.app.ecarepro.core.domain.model.message.InboxMessage
import com.app.ecarepro.core.domain.model.message.MessageSettings
import com.app.ecarepro.core.domain.model.message.SentMessage
import com.app.ecarepro.core.domain.model.message.SentRecipient

import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============== Message Settings ==============

@Serializable
data class NetworkMessageSettings(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("compose") val compose: Boolean? = null,
    @SerialName("msgWithSMS") val msgWithSMS: Boolean? = null,
    @SerialName("onlyMsg") val onlyMsg: Boolean? = null,
    @SerialName("isBoardingSchool") val isBoardingSchool: Boolean? = null,
) : NetworkResponse

fun NetworkMessageSettings.toDomainModel() = MessageSettings(
    compose = compose ?: false,
    msgWithSMS = msgWithSMS ?: false,
    onlyMsg = onlyMsg ?: false,
)

// ============== Inbox Message List ==============

@Serializable
data class NetworkInboxMessageDto(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("sender") val sender: List<NetworkInboxMessage>? = null,
    @SerialName("total") val total: Int = 0,
) : NetworkResponse

@Serializable
data class NetworkInboxMessage(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("childName") val childName: String? = null,
    @SerialName("className") val className: String? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("senderID") val senderID: Int? = null,
    @SerialName("senderType") val senderType: Int? = null,
    @SerialName("sentOn") val sentOn: String? = null,
    @SerialName("unread") val unread: Int? = null,
)

fun NetworkInboxMessage.toDomainModel() = InboxMessage(
    id = id ?: "",
    name = name ?: "",
    photo = photo ?: "",
    childName = childName,
    className = className,
    designation = designation,
    sentOn = sentOn ?: "",
    unreadCount = unread ?: 0,
)

// ============== Sent Message List ==============

@Serializable
data class NetworkSentMessageDto(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("sentMessages") val sentMessages: List<NetworkSentMessage>? = null,
    @SerialName("canDelete") val canDelete: Boolean? = null,
    @SerialName("total") val total: Int = 0,
) : NetworkResponse

@Serializable
data class NetworkSentMessage(
    @SerialName("id") val id: String? = null,
    @SerialName("subject") val subject: String? = null,
    @SerialName("sentOn") val sentOn: String? = null,
    @SerialName("msgType") val msgType: Int? = null,
    @SerialName("recipients") val recipients: List<NetworkSentRecipient>? = null,
    @SerialName("canDelete") val canDelete: Boolean? = null,
)

@Serializable
data class NetworkSentRecipient(
    @SerialName("name") val name: String? = null,
    @SerialName("designation") val designation: String? = null,
    @SerialName("photo") val photo: String? = null,
    @SerialName("childName") val childName: String? = null,
    @SerialName("className") val className: String? = null,
    @SerialName("receiverType") val receiverType: Int? = null,
    @SerialName("hasRead") val hasRead: Boolean? = null,
)

@Serializable
data class NetworkDeleteMessageResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
) : NetworkResponse

fun NetworkSentMessage.toDomainModel() = SentMessage(
    id = id ?: "",
    subject = subject,
    sentOn = sentOn ?: "",
    msgType = msgType,
    recipients = recipients?.map { it.toDomainModel() } ?: emptyList(),
    canDelete = canDelete ?: false,
)

fun NetworkSentRecipient.toDomainModel() = SentRecipient(
    name = name ?: "",
    designation = designation ?: "",
    photo = photo ?: "",
    childName = childName,
    className = className,
    receiverType = receiverType,
    hasRead = hasRead ?: false,
)

// ============== Conversation / Timeline ==============

@Serializable
data class NetworkConversationDto(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
    @SerialName("allMessages") val allMessages: List<NetworkConversationMessage>? = null,
    @SerialName("total") val total: Int = 0,
) : NetworkResponse

@Serializable
data class NetworkConversationMessage(
    @SerialName("id") val id: String? = null,
    @SerialName("subject") val subject: String? = null,
    @SerialName("sentOn") val sentOn: String? = null,
    @SerialName("msgType") val msgType: Int? = null,
    @SerialName("hasRead") val hasRead: Boolean? = null,
    @SerialName("abbreviation") val abbreviation: String? = null,
    @SerialName("msgID") val msgID: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("photo") val photo: String? = null,
)

fun NetworkConversationMessage.toDomainModel() = ConversationMessage(
    id = id ?: "",
    subject = subject,
    sentOn = sentOn ?: "",
    msgType = msgType,
    hasRead = hasRead ?: false,
    name = name ?: abbreviation ?: "",
    photo = photo ?: "",
)
