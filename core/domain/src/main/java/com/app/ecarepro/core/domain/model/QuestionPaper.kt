package com.app.ecarepro.core.domain.model

data class QuestionPaperResponse(
    val errorCode: Int,
    val status: String?,
    val message: String?,
    val academicYear: List<QPAcademicYear>?,
    val qPList: List<SubjectPapers>?,
)

data class QPAcademicYear(
    val yrID: Int,
    val session: String,
    val startDate: String?,
    val endDate: String?,
    val isCur: Boolean?,
)

data class SubjectPapers(
    val subID: Int?,
    val subjectName: String?,
    val questionPapers: List<QuestionPaperItem>?,
)

data class QuestionPaperItem(
    val examName: String?,
    val file: String?,
    val fileSize: String?,
    val updatedOn: String?,
)
