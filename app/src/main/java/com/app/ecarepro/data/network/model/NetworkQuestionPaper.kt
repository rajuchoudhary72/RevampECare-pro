package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AcademicYear

data class NetworkQuestionPaper(
    val academicYear: List<AcademicYear>,
    val errorCode: Int,
    val message: String,
    val qP_List: List<QP>,
    val status: String
)