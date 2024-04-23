package com.app.ecarepro.model

import com.app.ecarepro.model.Mark

data class Subject(
    val marks: List<Mark>,
    val subID: Int,
    val subjectName: String
)