package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.InboxMessageDto
import com.app.ecarepro.data.network.model.MessageSettings
import com.app.ecarepro.data.network.model.SentMessageDto
import retrofit2.http.GET
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

}