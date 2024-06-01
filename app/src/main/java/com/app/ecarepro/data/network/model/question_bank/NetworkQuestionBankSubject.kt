package com.app.ecarepro.data.network.model.question_bank

data class NetworkQuestionBankSubject(
    val errorCode: Int,
    val message: String,
    val status: String,
    val subjects: List<Subject>
)