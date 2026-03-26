package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.message.ConversationMessage
import com.app.ecarepro.core.domain.model.message.InboxMessage
import com.app.ecarepro.core.domain.model.message.MessageSettings
import com.app.ecarepro.core.domain.model.message.SentMessage
import com.app.ecarepro.core.domain.repository.MessageRepository
import com.app.ecarepro.core.network.MessageRemoteDataSource
import com.app.ecarepro.core.network.model.message.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class MessageRepositoryImpl @Inject constructor(
    private val messageRemoteDataSource: MessageRemoteDataSource,
) : MessageRepository {

    override fun getMessageSettings(): Flow<Result<MessageSettings>> = asResultFlow {
        messageRemoteDataSource.getMessageSettings().toDomainModel()
    }

    override fun getInboxMessages(page: Int): Flow<Result<Pair<List<InboxMessage>, Int>>> = asResultFlow {
        val response = messageRemoteDataSource.getInboxMessages(page)
        val messages = response.sender?.map { it.toDomainModel() } ?: emptyList()
        messages to response.total
    }

    override fun getSentMessages(page: Int, fromDate: String?, tillDate: String?): Flow<Result<Pair<List<SentMessage>, Int>>> = asResultFlow {
        val response = messageRemoteDataSource.getSentMessages(page, fromDate, tillDate)
        val messages = response.sentMessages?.map { it.toDomainModel() } ?: emptyList()
        messages to response.total
    }

    override fun deleteSentMessage(id: String): Flow<Result<Unit>> = asResultFlow {
        messageRemoteDataSource.deleteSentMessage(id)
    }

    override fun getConversations(
        page: Int,
        fromDate: String,
        toDate: String,
    ): Flow<Result<Pair<List<ConversationMessage>, Int>>> = asResultFlow {
        val response = messageRemoteDataSource.getConversations(page, fromDate, toDate)
        val messages = response.allMessages?.map { it.toDomainModel() } ?: emptyList()
        messages to response.total
    }
}
