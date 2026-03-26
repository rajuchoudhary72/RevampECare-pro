package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.MessageRemoteDataSource
import com.app.ecarepro.core.network.model.message.NetworkConversationDto
import com.app.ecarepro.core.network.model.message.NetworkInboxMessageDto
import com.app.ecarepro.core.network.model.message.NetworkMessageSettings
import com.app.ecarepro.core.network.model.message.NetworkSentMessageDto
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.MessageService
import javax.inject.Inject

internal class MessageRemoteDataSourceImpl @Inject constructor(
    private val messageService: MessageService,
) : MessageRemoteDataSource {

    override suspend fun getMessageSettings(): NetworkMessageSettings {
        val response = messageService.getMessageSettings()
        response.unwrapPayload { this }
        return response
    }

    override suspend fun getInboxMessages(page: Int): NetworkInboxMessageDto {
        val response = messageService.getInboxMessages(page)
        response.unwrapPayload { this }
        return response
    }

    override suspend fun getSentMessages(page: Int, fromDate: String?, tillDate: String?): NetworkSentMessageDto {
        val response = messageService.getSentMessages(page, fromDate, tillDate)
        response.unwrapPayload { this }
        return response
    }

    override suspend fun deleteSentMessage(id: String) {
        val response = messageService.deleteSentMessage(id)
        response.unwrapPayload { this }
    }

    override suspend fun getConversations(page: Int, fromDate: String, toDate: String): NetworkConversationDto {
        val response = messageService.getConversations(page, fromDate, toDate)
        response.unwrapPayload { this }
        return response
    }
}
