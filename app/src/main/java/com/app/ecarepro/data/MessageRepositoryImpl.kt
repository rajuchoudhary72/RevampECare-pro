package com.app.ecarepro.data

import com.app.ecarepro.data.network.model.BulkMessageRequestDto
import com.app.ecarepro.data.network.model.ClassContact
import com.app.ecarepro.data.network.model.ConversationDetailsDto
import com.app.ecarepro.data.network.model.GenerateTokenRequestDto
import com.app.ecarepro.data.network.model.InboxMessageDto
import com.app.ecarepro.data.network.model.MessageFormDto
import com.app.ecarepro.data.network.model.MessageSettings
import com.app.ecarepro.data.network.model.NetworkConversationReport
import com.app.ecarepro.data.network.model.NetworkStudentParentComms
import com.app.ecarepro.data.network.model.ReplyMessageRequestDto
import com.app.ecarepro.data.network.model.SendMessageRequest
import com.app.ecarepro.data.network.model.SentMessageDto
import com.app.ecarepro.data.network.model.SmsType
import com.app.ecarepro.data.network.model.StaffContactsDto
import com.app.ecarepro.data.network.model.StaffType
import com.app.ecarepro.data.network.service.MessageService
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.ui.message.chat.MessageType
import com.app.ecarepro.utils.Constant
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

    override fun getInboxMessages(pg: Int): Flow<Result<InboxMessageDto>> {
        return flow {
            try {
                val response = messageService.getInboxMessages(pg)
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

    override fun getSentMessages(
        pg: Int,
        fromDate: String?,
        tillDate: String?
    ): Flow<Result<SentMessageDto>> {
        return flow {
            try {
                val response = messageService.getSentMessages(pg, fromDate, tillDate)
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



    override fun getConversationDetails(
        id: String,
        messageType: MessageType
    ): Flow<Result<ConversationDetailsDto>> {
        return flow {
            try {
                val response =
                    when (messageType) {
                        MessageType.INBOX -> {
                            messageService.getConversationDetails(id)
                        }
                        MessageType.CONV -> {
                            messageService.getConversationMsgDTL(id)
                        }
                        else -> messageService.getSentConversationDetails( id )
                    }

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



    override fun replyMessage(request: ReplyMessageRequestDto): Flow<Result<String>> {
        return flow {
            try {
                val response = messageService.replyMessage(request)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message!!))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getStaffTypes(): Flow<Result<List<StaffType>>> {
        return flow {
            try {
                val response = messageService.getStaffTypes()
                if (response.errorCode == 0) {
                    emit(Result.success(response.staffType ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getStaffContacts(staffTypeIDs: List<Int>?): Flow<Result<StaffContactsDto>> {
        return flow {
            try {
                val response =
                    messageService.getStaffContacts(staffTypeIDs?.joinToString { it.toString() })
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

    override fun getClassContacts(
        ofUserType: Int,
        scholarType: Int,
    ): Flow<Result<List<ClassContact>>> {
        return flow {
            try {
                val response = messageService.getContactsWithClasses(ofUserType, scholarType)
                if (response.errorCode == 0) {
                    emit(Result.success(response.classContacts ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getSmsTemplates(): Flow<Result<List<SmsType>>> {
        return flow {
            try {
                val response = messageService.getSmsTemplates()
                if (response.errorCode == 0) {
                    emit(Result.success(response.smsType ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                // emit(Result.failure(error))
            }
        }
    }

    override fun sendBulkMessage(request: BulkMessageRequestDto): Flow<Result<String>> {
        return flow {
            try {
                val token = messageService.generateToken(
                    Constant.SMS_TOKEN_URL,
                    GenerateTokenRequestDto()
                )
                val response = messageService.sendBulkMessage(
                    Constant.SMS_BULK_MSG_URL,
                    token.authenticationToken,
                    request
                )
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: "Success"))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun sendMessage(request: SendMessageRequest): Flow<Result<String>> {
        return flow {
            try {
                val response = messageService.sendMessage(request)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: "Success"))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }


    override suspend fun getConversationReport(
        pg: Int,
        fromDate: String?,
        tillDate: String?
    ): NetworkConversationReport {
        return messageService.getConversationReport(pg, fromDate, tillDate)
    }

    override suspend fun studentParentComms(
        recipientType: Int,
        classIDs: String,
        scholarType: Int,
        byRollNo: Boolean
    ): NetworkStudentParentComms {
        return messageService.studentParentComms(recipientType, classIDs, scholarType, byRollNo)
    }

}


