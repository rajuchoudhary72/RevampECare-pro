package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.conversation.NetworkConversationDetailResponse
import com.app.ecarepro.core.network.model.conversation.NetworkConversationListResponse
import com.app.ecarepro.core.network.model.message.NetworkConversationDto
import com.app.ecarepro.core.network.model.message.NetworkDeleteMessageResponse
import com.app.ecarepro.core.network.model.message.NetworkInboxMessageDto
import com.app.ecarepro.core.network.model.message.NetworkMessageSettings
import com.app.ecarepro.core.network.model.message.NetworkSentMessageDto
import com.app.ecarepro.core.network.model.CommonNetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MessageService {

    @GET("Message/Setting")
    suspend fun getMessageSettings(): NetworkMessageSettings

    @GET("Message/Inbox")
    suspend fun getInboxMessages(
        @Query("pg") page: Int,
    ): NetworkInboxMessageDto

    @GET("Message/SentMessages")
    suspend fun getSentMessages(
        @Query("pg") page: Int,
        @Query("fromDate") fromDate: String? = null,
        @Query("TillDate") tillDate: String? = null,
    ): NetworkSentMessageDto

    @GET("Message/DeleteMessage")
    suspend fun deleteSentMessage(
        @Query("ID") id: String,
    ): NetworkDeleteMessageResponse

    @GET("Message/Conversation")
    suspend fun getConversations(
        @Query("pg") page: Int,
        @Query("FromDate") fromDate: String,
        @Query("ToDate") toDate: String,
    ): NetworkConversationDto

    @GET("Message/Conversation")
    suspend fun getConversationReport(
        @Query("FromDate") fromDate: String,
        @Query("ToDate") toDate: String,
    ): NetworkConversationListResponse

    @GET("Message/FilterConversation")
    suspend fun getFilteredConversations(
        @Query("FromDate") fromDate: String,
        @Query("TillDate") tillDate: String,
        @Query("Sender") sender: Int? = null,
        @Query("Hasword") hasWord: String? = null,
    ): NetworkConversationListResponse

    @GET("Message/ConversationMsgDTL")
    suspend fun getConversationDetail(
        @Query("MsgID") msgId: String,
    ): NetworkConversationDetailResponse

    @GET("Message/DeleteConversation")
    suspend fun deleteConversation(
        @Query("ID") id: String,
        @Query("Device") device: Int = 1,
    ): CommonNetworkResponse
}
