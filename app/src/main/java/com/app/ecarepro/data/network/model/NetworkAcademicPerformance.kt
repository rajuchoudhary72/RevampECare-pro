package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.ExamSystem

data class NetworkAcademicPerformance(
    val errorCode: Int,
    val examSystems: List<ExamSystem>,
    val message: String,
    val status: String
)