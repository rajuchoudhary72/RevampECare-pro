package com.app.ecarepro.data.network.model.question_bank

data class NetworkQuestionBankCreate(
    val errorCode: Int,
    val message: String,
    val myClasses: List<MyClasse>,
    val questionTypes: List<QuestionType>,
    val status: String
)