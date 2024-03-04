package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.ConversationDetailsDto
import com.app.ecarepro.data.network.model.InboxMessageDto
import com.app.ecarepro.data.network.model.MessageFormDto
import com.app.ecarepro.data.network.model.MessageSettings
import com.app.ecarepro.data.network.model.SentMessageDto
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessageSettings(): Flow<Result<MessageSettings>>

    fun getInboxMessages(pg: Int): Flow<Result<InboxMessageDto>>

    fun getSentMessages(
        pg: Int,
        fromDate: String? = null,
        tillDate: String? = null
    ): Flow<Result<SentMessageDto>>

    fun getConversation(
        pg: Int,
        id: String,
        query: String? = null,
    ): Flow<Result<MessageFormDto>>

    fun getConversationDetails(
        id: String,
    ): Flow<Result<ConversationDetailsDto>>
}