package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AllTeacher
import com.app.ecarepro.model.SubjectTeacher

data class NetworkAllTeacher(
    val allTeacher: List<AllTeacher>,
    val classTeacher: Any,
    val errorCode: Int,
    val message: String,
    val status: String,
    val subjectTeacher: List<SubjectTeacher>,
    val teachers: Any
)