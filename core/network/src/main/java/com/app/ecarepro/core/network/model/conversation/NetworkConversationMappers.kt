package com.app.ecarepro.core.network.model.conversation

import com.app.ecarepro.core.domain.model.conversation.Conversation
import com.app.ecarepro.core.domain.model.conversation.ConversationMessage
import com.app.ecarepro.core.domain.model.conversation.ConversationRecipient
import com.app.ecarepro.core.domain.model.conversation.ConversationSender

fun NetworkConversationItem.toDomainModel() = Conversation(
    msgType = msgType ?: 1,
    abbreviation = abbreviation?.trim().orEmpty(),
    sentOn = sentOn.orEmpty(),
    readCount = readCount ?: 0,
    msgID = msgID.orEmpty(),
    subject = subject.orEmpty(),
    isReplyMsg = isReplyMsg ?: 0,
    sender = senderDTL?.toDomainModel(),
    recipients = recipients?.map { it.toDomainModel() }.orEmpty(),
)

fun NetworkConversationSender.toDomainModel() = ConversationSender(
    sndrId = sndrId,
    name = name.orEmpty(),
    designation = designation.orEmpty(),
    photo = photo,
    senderType = senderType ?: 0,
    childName = childName,
    className = className,
    senderID = senderID ?: 0,
)

fun NetworkConversationRecipient.toDomainModel() = ConversationRecipient(
    name = name.orEmpty(),
    childName = childName,
    className = className?.trim(),
    designation = designation,
    photo = photo,
    hasRead = hasRead ?: false,
    readAt = readAt,
    receiverType = receiverType ?: 0,
    receiverID = receiverID ?: 0,
    rcvID = rcvID,
    rollNumber = rollNumber,
    admissionNo = admissionNo,
    mobile = mobile,
)

fun NetworkConversationMessage.toDomainModel() = ConversationMessage(
    msgType = msgType ?: 1,
    sentOn = sentOn.orEmpty(),
    isMine = isMine ?: false,
    rplID = rplID ?: 0,
    msgID = msgID ?: 0,
    sender = senderDTL?.toDomainModel(),
    body = body?.trim().orEmpty(),
    filePaths = filePaths.orEmpty(),
)
