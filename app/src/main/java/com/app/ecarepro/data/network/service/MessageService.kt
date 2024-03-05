package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.ConversationDetailsDto
import com.app.ecarepro.data.network.model.InboxMessageDto
import com.app.ecarepro.data.network.model.MessageFormDto
import com.app.ecarepro.data.network.model.MessageSettings
import com.app.ecarepro.data.network.model.ReplyMessageRequestDto
import com.app.ecarepro.data.network.model.ReplyMessageResponseDto
import com.app.ecarepro.data.network.model.SentMessageDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MessageService {
    @GET("Message/Setting")
    suspend fun getMessageSettings(): MessageSettings

    @GET("Message/Inbox")
    suspend fun getInboxMessages(@Query("pg") pg: Int): InboxMessageDto

    @GET("Message/SentMessages")
    suspend fun getSentMessages(
        @Query("pg") pg: Int,
        @Query("fromDate") fromDate: String? = null,
        @Query("TillDate") tillDate: String? = null,
    ): SentMessageDto

    @GET("Message/From")
    suspend fun getConversation(
        @Query("pg") pg: Int,
        @Query("ID") id: String
    ): MessageFormDto

    @GET("Message/SearchInbox")
    suspend fun searchConversation(
        @Query("pg") pg: Int,
        @Query("ID") id: String,
        @Query("Query") query: String? = null,
    ): MessageFormDto

    @GET("Message/MsgDTL")
    suspend fun getConversationDetails(
        @Query("ID") id: String
    ): ConversationDetailsDto

    @GET("Message/SentMsgDTL")
    suspend fun getSentConversationDetails(
        @Query("ID") id: String
    ): ConversationDetailsDto

    @POST("Message/ReplyMessage")
    suspend fun replyMessage(
        @Body request: ReplyMessageRequestDto
    ): ReplyMessageResponseDto

}