package com.app.ecarepro.data

import com.app.ecarepro.data.network.model.InboxMessage
import com.app.ecarepro.data.network.model.MessageFormDto
import com.app.ecarepro.data.network.model.MessageSettings
import com.app.ecarepro.data.network.model.SentMessage
import com.app.ecarepro.data.network.service.MessageService
import com.app.ecarepro.data.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val messageService: MessageService
) : MessageRepository {
    override fun getMessageSettings(): Flow<Result<MessageSettings>> {
        return flow {
            try {
                val response = messageService.getMessageSettings()
                if (response.errorCode == 0) {
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getInboxMessages(pg: Int): Flow<Result<List<InboxMessage>>> {
        return flow {
            try {
                val response = messageService.getInboxMessages(pg)
                if (response.errorCode == 0) {
                    emit(Result.success(response.sender ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getSentMessages(
        pg: Int,
        fromDate: String?,
        tillDate: String?
    ): Flow<Result<List<SentMessage>>> {
        return flow {
            try {
                val response = messageService.getSentMessages(pg, fromDate, tillDate)
                if (response.errorCode == 0) {
                    emit(Result.success(response.sentMessages ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getConversation(
        pg: Int,
        id: String,
        query: String?
    ): Flow<Result<MessageFormDto>> {
        return flow {
            try {
                val response =
                    if (query.isNullOrEmpty())
                        messageService.getConversation(pg, id)
                    else
                        messageService.searchConversation(pg, id, query)
                if (response.errorCode == 0) {
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }
}