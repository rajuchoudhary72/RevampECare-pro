package com.app.ecarepro.data.network.model.question_bank

data class NetworkQuestionBankChapters(
    val chapters: List<Chapter>,
    val errorCode: Int,
    val message: String,
    val status: String
)