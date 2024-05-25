package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.LessonPlan
import com.app.ecarepro.model.RequiredField

data class NetworkLessonPlanDTL(
    val auditorLst: Any,
    val errorCode: Int,
    val lessonPlans: LessonPlan,
    val message: String,
    val requiredField: RequiredField,
    val status: String,
    val subjects: Any,
    val totalRecord: Int
)