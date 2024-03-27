package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.ClassContact
import com.app.ecarepro.data.network.model.ConversationDetailsDto
import com.app.ecarepro.data.network.model.InboxMessageDto
import com.app.ecarepro.data.network.model.MessageFormDto
import com.app.ecarepro.data.network.model.MessageSettings
import com.app.ecarepro.data.network.model.ReplyMessageRequestDto
import com.app.ecarepro.data.network.model.SentMessageDto
import com.app.ecarepro.data.network.model.StaffContactsDto
import com.app.ecarepro.data.network.model.StaffType
import com.app.ecarepro.ui.message.chat.MessageType
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessageSettings(): Flow<Result<MessageSettings>>

    fun getInboxMessages(pg: Int): Flow<Result<InboxMessageDto>>

    fun getSentMessages(
        pg: Int,
        fromDate: String? = null,
        tillDate: String? = null
    ): Flow<Result<SentMessageDto>>

    fun getConversation(
        pg: Int,
        id: String,
        query: String? = null,
    ): Flow<Result<MessageFormDto>>

    fun getConversationDetails(
        id: String,
        messageType: MessageType
    ): Flow<Result<ConversationDetailsDto>>

    fun replyMessage(
        request: ReplyMessageRequestDto
    ): Flow<Result<String>>

    fun getStaffTypes(): Flow<Result<List<StaffType>>>

    fun getStaffContacts(staffTypeIDs: List<Int>?): Flow<Result<StaffContactsDto>>

    fun getClassContacts(
        ofUserType: Int, scholarType: Int,
    ): Flow<Result<List<ClassContact>>>
}