package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Conversation

data class NetworkConversationReport(
    val conversation: List<Conversation>,
    val errorCode: Int,
    val message: String,
    val status: String,
    val total: Int
)