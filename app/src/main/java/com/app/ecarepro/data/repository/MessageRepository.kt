package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.InboxMessage
import com.app.ecarepro.data.network.model.MessageSettings
import com.app.ecarepro.data.network.model.SentMessage
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessageSettings(): Flow<Result<MessageSettings>>

    fun getInboxMessages(pg: Int): Flow<Result<List<InboxMessage>>>

    fun getSentMessages(
        pg: Int,
        fromDate: String? = null,
        tillDate: String? = null
    ): Flow<Result<List<SentMessage>>>
}