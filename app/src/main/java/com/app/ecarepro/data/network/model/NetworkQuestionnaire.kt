package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Question

data class NetworkQuestionnaire(
    val errorCode: Int,
    val message: String,
    val questions: List<Question>,
    val status: String,
    val total: Int
)