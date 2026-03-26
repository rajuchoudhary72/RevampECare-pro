package com.app.ecarepro.core.domain.model.survey

data class SurveyAnswerRequest(
    val id: String,
    val questions: List<SurveyQuestionAnswerPayload>,
)

data class SurveyQuestionAnswerPayload(
    val queID: Int,
    val question: String,
    val isMultiSelect: Boolean,
    val isAnsMandatory: Boolean,
    val response: Int,
    val answer: String = "",
    val options: List<SurveyOptionAnswerPayload>,
)

data class SurveyOptionAnswerPayload(
    val optID: Int,
    val option: String,
    val isSelected: Boolean,
    val response: Int,
)
