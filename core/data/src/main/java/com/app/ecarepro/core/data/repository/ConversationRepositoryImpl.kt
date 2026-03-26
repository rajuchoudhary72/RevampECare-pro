package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.conversation.Conversation
import com.app.ecarepro.core.domain.model.conversation.ConversationDetail
import com.app.ecarepro.core.domain.repository.ConversationRepository
import com.app.ecarepro.core.network.ConversationRemoteDataSource
import com.app.ecarepro.core.network.model.conversation.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ConversationRepositoryImpl @Inject constructor(
    private val dataSource: ConversationRemoteDataSource,
) : ConversationRepository {

    override fun getConversations(fromDate: String, toDate: String): Flow<Result<Pair<List<Conversation>, Boolean>>> =
        asResultFlow {
            val (items, canDelete) = dataSource.getConversations(fromDate, toDate)
            items.map { it.toDomainModel() } to canDelete
        }

    override fun getFilteredConversations(fromDate: String, tillDate: String, sender: Int?, hasWord: String?): Flow<Result<List<Conversation>>> =
        asResultFlow {
            dataSource.getFilteredConversations(fromDate, tillDate, sender, hasWord).map { it.toDomainModel() }
        }

    override fun getConversationDetail(msgId: String): Flow<Result<ConversationDetail>> =
        asResultFlow {
            val response = dataSource.getConversationDetail(msgId)
            ConversationDetail(
                subject = response.subject.orEmpty(),
                msgID = response.msgID ?: 0,
                readCount = response.readCount ?: 0,
                canReply = response.canReply ?: false,
                recipients = response.recipients?.map { it.toDomainModel() }.orEmpty(),
                messages = response.msgDTL?.map { it.toDomainModel() }.orEmpty(),
                firstSender = response.msgDTL?.firstOrNull()?.senderDTL?.toDomainModel(),
            )
        }

    override fun deleteConversation(id: String): Flow<Result<String>> =
        asResultFlow { dataSource.deleteConversation(id) }
}
