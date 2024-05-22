package com.app.ecarepro.ui.survey

data class Option(
    var isSelected: Boolean,
    val optID: Int?,
    val option: String?,
    val resDTL: Any?,
    val response: Int?
)