package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.TeacherAssignment

data class NetworkTeacherAssignment(
    val assignments: List<TeacherAssignment>,
    val errorCode: Int,
    val message: String,
    val status: String,
    val isReportView:Boolean
)