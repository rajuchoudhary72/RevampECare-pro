package com.app.ecarepro.core.domain.model.survey

data class SurveyItem(
    val id: String,
    val title: String,
    val description: String,
    val publishedOn: String,
    val openEndDate: String,
    val isOpen: Boolean,
    val isResponded: Boolean,
    val respondedOn: String?,
    val resultDeclared: Boolean,
)
