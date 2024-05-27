package com.app.ecarepro.ui.survey

data class AllSurvey(
    val description: String,
    val id: String,
    val isOpen: Boolean,
    val isResponded: Boolean,
    val openEndDate: String,
    val publishedOn: String?,
    val respondedOn: String?,
    val resultDeclared: Boolean,
    val title: String
)