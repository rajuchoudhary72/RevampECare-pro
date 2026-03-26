package com.app.ecarepro.core.domain.model.conversation

data class Conversation(
    val msgType: Int,
    val abbreviation: String,
    val sentOn: String,
    val readCount: Int,
    val msgID: String,
    val subject: String,
    val isReplyMsg: Int,
    val sender: ConversationSender?,
    val recipients: List<ConversationRecipient>,
)

data class ConversationSender(
    val sndrId: String?,
    val name: String,
    val designation: String,
    val photo: String?,
    val senderType: Int,
    val childName: String?,
    val className: String?,
    val senderID: Int,
)

data class ConversationRecipient(
    val name: String,
    val childName: String?,
    val className: String?,
    val designation: String?,
    val photo: String?,
    val hasRead: Boolean,
    val readAt: String?,
    val receiverType: Int,
    val receiverID: Int,
    val rcvID: String?,
    val rollNumber: String?,
    val admissionNo: String?,
    val mobile: String?,
)

data class ConversationDetail(
    val subject: String,
    val msgID: Int,
    val readCount: Int,
    val canReply: Boolean,
    val recipients: List<ConversationRecipient>,
    val messages: List<ConversationMessage>,
    val firstSender: ConversationSender?,
)

data class ConversationMessage(
    val msgType: Int,
    val sentOn: String,
    val isMine: Boolean,
    val rplID: Int,
    val msgID: Int,
    val sender: ConversationSender?,
    val body: String,
    val filePaths: List<String>,
)
