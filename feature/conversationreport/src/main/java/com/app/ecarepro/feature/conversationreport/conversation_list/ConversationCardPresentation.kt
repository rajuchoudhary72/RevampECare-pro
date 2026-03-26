package com.app.ecarepro.feature.conversationreport.conversation_list

import androidx.compose.runtime.Immutable
import com.app.ecarepro.core.domain.model.conversation.Conversation
import java.text.SimpleDateFormat
import java.util.Locale

@Immutable
data class ConversationCardPresentation(
    val id: String,
    val subject: String,
    val messagePreview: String,
    val senderName: String,
    val senderDesignation: String,
    val senderPhoto: String?,
    val sentDate: String,
    val sentToName: String,
    val readCount: Int,
    val totalRecipients: Int,
    val readPercentage: Double,
    val msgType: Int,
) {
    companion object {
        fun from(conversation: Conversation): ConversationCardPresentation {
            val recipients = conversation.recipients
            val sentTo = when {
                recipients.size > 1 -> "${recipients.first().name}, more"
                recipients.isNotEmpty() -> recipients.first().name
                else -> ""
            }
            val pct = if (recipients.isNotEmpty()) {
                kotlin.math.round((conversation.readCount * 100.0 / recipients.size) * 100) / 100.0
            } else 0.0

            return ConversationCardPresentation(
                id = conversation.msgID,
                subject = conversation.subject,
                messagePreview = conversation.abbreviation,
                senderName = conversation.sender?.name.orEmpty(),
                senderDesignation = conversation.sender?.designation.orEmpty(),
                senderPhoto = conversation.sender?.photo,
                sentDate = formatDate(conversation.sentOn),
                sentToName = sentTo,
                readCount = conversation.readCount,
                totalRecipients = recipients.size,
                readPercentage = pct,
                msgType = conversation.msgType,
            )
        }

        private fun formatDate(sentOn: String): String {
            return try {
                val input = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.US)
                val output = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                output.format(input.parse(sentOn)!!)
            } catch (e: Exception) {
                sentOn
            }
        }
    }
}

@Immutable
data class ConversationFilterState(
    val selectedSender: Int = 0,
    val selectedRecipient: Int = 0,
    val hasWordMode: HasWordMode = HasWordMode.ANY,
    val hasWordText: String = "",
) {
    val isActive: Boolean
        get() = selectedSender != 0 || selectedRecipient != 0 || hasWordMode == HasWordMode.SPECIFIC

    fun toSenderParam(): Int? = if (selectedSender > 0) selectedSender + 1 else null
    fun toHasWordParam(): String? = if (hasWordMode == HasWordMode.SPECIFIC && hasWordText.isNotBlank()) hasWordText else null
}

enum class HasWordMode { ANY, SPECIFIC }
