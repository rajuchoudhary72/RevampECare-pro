package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.LessonPlan

data class NetworkLessonPlanList(
    val errorCode: Int,
    val lessonPlans: List<LessonPlan>,
    val message: String,
    val status: String,
    val totalRecord: Int
)