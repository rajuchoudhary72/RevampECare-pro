package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.conversation.Conversation
import com.app.ecarepro.core.domain.model.conversation.ConversationDetail
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getConversations(fromDate: String, toDate: String): Flow<Result<Pair<List<Conversation>, Boolean>>>
    fun getFilteredConversations(fromDate: String, tillDate: String, sender: Int?, hasWord: String?): Flow<Result<List<Conversation>>>
    fun getConversationDetail(msgId: String): Flow<Result<ConversationDetail>>
    fun deleteConversation(id: String): Flow<Result<String>>
}
