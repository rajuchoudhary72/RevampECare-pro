package com.app.ecarepro.ui.statical

data class StaticGraphResponse(
    val errorCode: Int?,
    val message: String?,
    val staffStatistical: StaffStatistical?,
    val status: String?,
    val studentStatistical: StudentStatistical?,
    val teacherStatistical: TeacherStatistical?,
    val transporStatistical: TransporStatistical?
)