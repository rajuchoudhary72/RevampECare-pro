package com.app.ecarepro.core.domain.model.survey

data class SurveyQuestion(
    val queID: Int,
    val id: String,
    val question: String,
    val isMultiSelect: Boolean,
    val isAnsMandatory: Boolean,
    val response: Int,
    val textBoxOnly: Boolean,
    val answer: String?,
    val options: List<SurveyOption>,
)

data class SurveyOption(
    val optID: Int,
    val option: String,
    val isSelected: Boolean,
    val response: Int,
)
