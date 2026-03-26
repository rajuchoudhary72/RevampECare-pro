package com.app.ecarepro.core.domain.model.message

data class MessageSettings(
    val compose: Boolean,
    val msgWithSMS: Boolean,
    val onlyMsg: Boolean,
)

data class InboxMessage(
    val id: String,
    val name: String,
    val photo: String,
    val childName: String?,
    val className: String?,
    val designation: String?,
    val sentOn: String,
    val unreadCount: Int,
)

data class SentMessage(
    val id: String,
    val subject: String?,
    val sentOn: String,
    val msgType: Int?,
    val recipients: List<SentRecipient>,
    val canDelete: Boolean,
)

data class SentRecipient(
    val name: String,
    val designation: String,
    val photo: String,
    val childName: String?,
    val className: String?,
    val receiverType: Int?,
    val hasRead: Boolean,
)

data class ConversationMessage(
    val id: String,
    val subject: String?,
    val sentOn: String,
    val msgType: Int?,
    val hasRead: Boolean,
    val name: String,
    val photo: String,
)
