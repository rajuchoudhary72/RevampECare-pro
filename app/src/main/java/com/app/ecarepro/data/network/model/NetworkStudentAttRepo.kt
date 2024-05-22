package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.model.StuAttendanceRepo

data class NetworkStudentAttRepo(
    val absentDays: Int,
    val academicYears: List<AcademicYear>,
    val attendance: List<StuAttendanceRepo>,
    val errorCode: Int,
    val isLateEnable: Boolean,
    val lateDays: Int,
    val leaveDays: Int,
    val message: String,
    val photo: String,
    val presentDays: Int,
    val schoolDays: Int,
    val status: String,
    val studentName: String,
    val totalAbsent: Int,
    val totalLates: Int,
    val totalLeave: Int,
    val totalPresent: Int,
    val workingDays: Int
)