package com.app.ecarepro.model

data class ExamSystem(
    val exmStmID: Int,
    val exmStmName: String,
    val subjets: List<Subject>
)