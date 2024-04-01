package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.BulkMessageRequestDto
import com.app.ecarepro.data.network.model.BulkMessageResponseDto
import com.app.ecarepro.data.network.model.ContactWithClassDto
import com.app.ecarepro.data.network.model.ConversationDetailsDto
import com.app.ecarepro.data.network.model.GenerateTokenRequestDto
import com.app.ecarepro.data.network.model.GenerateTokenResponseDto
import com.app.ecarepro.data.network.model.InboxMessageDto
import com.app.ecarepro.data.network.model.MessageFormDto
import com.app.ecarepro.data.network.model.MessageSettings
import com.app.ecarepro.data.network.model.ReplyMessageRequestDto
import com.app.ecarepro.data.network.model.ReplyMessageResponseDto
import com.app.ecarepro.data.network.model.SentMessageDto
import com.app.ecarepro.data.network.model.SmsTemplatesDto
import com.app.ecarepro.data.network.model.StaffContactsDto
import com.app.ecarepro.data.network.model.StaffTypeDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

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

    @GET("Staff/Types")
    suspend fun getStaffTypes(): StaffTypeDto

    @GET("Message/StaffContact")
    suspend fun getStaffContacts(
        @Query("StaffTypeIDs") ofUserType: String? = null,
    ): StaffContactsDto

    @GET("Message/StudentParentContactClassWise")
    suspend fun getContactsWithClasses(
        @Query("OfUserType") ofUserType: Int,
        @Query("ScholarType") scholarType: Int,
    ): ContactWithClassDto
    @GET("Message/SMSTemplates")
    suspend fun getSmsTemplates(
    ): SmsTemplatesDto

    @POST
    suspend fun generateToken(
        @Url url: String,
        @Body request: GenerateTokenRequestDto
    ): GenerateTokenResponseDto

    @POST
    suspend fun sendBulkMessage(
        @Url url: String,
        @Header("AuthenticationToken") token: String,
        @Body request: BulkMessageRequestDto
    ): BulkMessageResponseDto
}