package com.app.ecarepro.ui.survey

data class SurveyQuestionsResponse(
    val errorCode: Int?,
    val message: String?,
    val questions: ArrayList<Question>,
    val status: String?,
    val surID: Int?,
    val title: String?
)

data class SurveyQuestionsSubmitRequest(
    var questions: ArrayList<Question>,
    var id: String?,

    )