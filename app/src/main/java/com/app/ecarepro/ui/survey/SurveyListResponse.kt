package com.app.ecarepro.ui.survey

data class SurveyListResponse(
    val allSurvey: List<AllSurvey>,
    val errorCode: Int?,
    val message: String?,
    val status: String?,
    val total: Int?
)