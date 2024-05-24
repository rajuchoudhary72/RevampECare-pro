package com.app.ecarepro.ui.award

data class ExcellenceAwardResponse(
    val academicActivity: List<AcademicActivity?>?,
    val className: String?,
    val errorCode: Int?,
    val grandTotal: String?,
    val headerName: Any?,
    val rollNumber: String?,
    val schImgURL: String?,
    val sportActivity: List<SportActivity?>?,
    val stName: String?,
    val status: Any?,
    val totalMarksInAcademic: String?,
    val totalMarksInSport: String?,
    val year: String?
)

data class AcademicActivity(
    val activity: String,
    val marks: String
)

data class SportActivity(
    val activity: String,
    val marks: String
)