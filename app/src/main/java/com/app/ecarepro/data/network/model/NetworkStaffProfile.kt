package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AcademicYear
import com.app.ecarepro.model.AttendanceDTL
import com.app.ecarepro.model.Details
import com.app.ecarepro.model.SalaryStructure
import com.app.ecarepro.model.TimetableSummary

data class NetworkStaffProfile(
    val academicYears: List<AcademicYear>,
    val advanceGiven: String,
    val assignments: Int,
    val attendanceDTL: AttendanceDTL,
    val attendanceEnabled: Boolean,
    val attendancePer: String,
    val currentSalary: String,
    val details: Details,
    val errorCode: Int,
    val leaveRecord: String,
    val message: String,
    val salaryEnabled: Boolean,
    val salaryStructure: SalaryStructure,
    val sentMessage: Int,
    val status: String,
    val timetableEnabled: Boolean,
    val timetableSummary: TimetableSummary,
    val workLoad: String
)