package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.ConversationRemoteDataSource
import com.app.ecarepro.core.network.model.conversation.NetworkConversationDetailResponse
import com.app.ecarepro.core.network.model.conversation.NetworkConversationItem
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.MessageService
import javax.inject.Inject

internal class ConversationRemoteDataSourceImpl @Inject constructor(
    private val service: MessageService,
) : ConversationRemoteDataSource {

    override suspend fun getConversations(fromDate: String, toDate: String): Pair<List<NetworkConversationItem>, Boolean> {
        val response = service.getConversationReport(fromDate, toDate)
        val items = response.unwrapPayload { conversation.orEmpty() }
        return items to (response.canDeleteConv ?: false)
    }

    override suspend fun getFilteredConversations(fromDate: String, tillDate: String, sender: Int?, hasWord: String?): List<NetworkConversationItem> =
        service.getFilteredConversations(fromDate, tillDate, sender, hasWord)
            .unwrapPayload { conversation.orEmpty() }

    override suspend fun getConversationDetail(msgId: String): NetworkConversationDetailResponse {
        val response = service.getConversationDetail(msgId)
        if (response.errorCode != 0) throw Exception(response.message)
        return response
    }

    override suspend fun deleteConversation(id: String): String {
        val response = service.deleteConversation(id)
        if (response.status != "ok") throw Exception(response.message)
        return response.message
    }
}
