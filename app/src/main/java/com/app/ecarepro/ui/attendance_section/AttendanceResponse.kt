package com.app.ecarepro.ui.attendance_section

data class AttendanceResponse(
    val absentDays: Int?,
    val academicYears: List<AcademicYear>,
    val attendance: List<Attendance>?,
    val errorCode: Int?,
    val isLateEnable: Boolean?,
    val lateDays: Int?,
    val leaveDays: Int?,
    val message: String?,
    val photo: String?,
    val presentDays: Int?,
    val present_WH_Days: Int?,
    val whDays: Int?,
    val schoolDays: Int?,
    val status: String?,
    val studentName: String?,
    val totalAbsent: Int?,
    val totalLates: Int?,
    val totalLeave: Int?,
    val totalPresent: Int?,
    val totalPresent_WH: Int?,
    val totalWH: Int?,
    val workingDays: Int?
)