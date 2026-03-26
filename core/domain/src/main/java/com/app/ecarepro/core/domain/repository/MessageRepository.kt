package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.message.ConversationMessage
import com.app.ecarepro.core.domain.model.message.InboxMessage
import com.app.ecarepro.core.domain.model.message.MessageSettings
import com.app.ecarepro.core.domain.model.message.SentMessage
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessageSettings(): Flow<Result<MessageSettings>>
    fun getInboxMessages(page: Int): Flow<Result<Pair<List<InboxMessage>, Int>>>
    fun getSentMessages(page: Int, fromDate: String? = null, tillDate: String? = null): Flow<Result<Pair<List<SentMessage>, Int>>>
    fun deleteSentMessage(id: String): Flow<Result<Unit>>
    fun getConversations(
        page: Int,
        fromDate: String,
        toDate: String,
    ): Flow<Result<Pair<List<ConversationMessage>, Int>>>
}
