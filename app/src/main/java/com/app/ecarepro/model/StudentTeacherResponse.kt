package com.app.ecarepro.model

data class StudentTeacherResponse(
    val allTeacher: List<AllTeacher>,
    val classTeacher: Any?,
    val errorCode: Int?,
    val message: String?,
    val status: String?,
    val subjectTeacher: List<AllTeacher>,
    val teachers: Any?
)