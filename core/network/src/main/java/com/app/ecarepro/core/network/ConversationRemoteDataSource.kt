package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.conversation.NetworkConversationDetailResponse
import com.app.ecarepro.core.network.model.conversation.NetworkConversationItem

interface ConversationRemoteDataSource {
    suspend fun getConversations(fromDate: String, toDate: String): Pair<List<NetworkConversationItem>, Boolean>
    suspend fun getFilteredConversations(fromDate: String, tillDate: String, sender: Int?, hasWord: String?): List<NetworkConversationItem>
    suspend fun getConversationDetail(msgId: String): NetworkConversationDetailResponse
    suspend fun deleteConversation(id: String): String
}
