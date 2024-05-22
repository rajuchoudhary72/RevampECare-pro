package com.app.ecarepro.data.network.model

data class QP(
    val questionPapers: List<QuestionPaper>,
    val subID: Int,
    val subjectName: String
)