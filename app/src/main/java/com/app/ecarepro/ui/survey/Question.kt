package com.app.ecarepro.ui.survey

data class Question(
    val isAnsMandatory: Boolean,
    val isMultiSelect: Boolean,
    var isSelected: Boolean=false,
    val Option: String,
    val options: ArrayList<Option>,
    val queID: Int?,
    val question: String?,
    val response: Int?
)