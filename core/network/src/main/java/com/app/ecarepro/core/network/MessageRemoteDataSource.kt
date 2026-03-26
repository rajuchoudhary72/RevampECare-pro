package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.message.NetworkConversationDto
import com.app.ecarepro.core.network.model.message.NetworkInboxMessageDto
import com.app.ecarepro.core.network.model.message.NetworkMessageSettings
import com.app.ecarepro.core.network.model.message.NetworkSentMessageDto

interface MessageRemoteDataSource {
    suspend fun getMessageSettings(): NetworkMessageSettings
    suspend fun getInboxMessages(page: Int): NetworkInboxMessageDto
    suspend fun getSentMessages(page: Int, fromDate: String? = null, tillDate: String? = null): NetworkSentMessageDto
    suspend fun deleteSentMessage(id: String)
    suspend fun getConversations(page: Int, fromDate: String, toDate: String): NetworkConversationDto
}
