package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.StudentProfile
import com.app.ecarepro.core.domain.model.StudentProfileDetailsResponse
import kotlinx.coroutines.flow.Flow

interface StudentProfileRepository {
    fun getStudentProfiles(scholarType: Int?): Flow<Result<List<StudentProfile>>>
    suspend fun getStudentProfileDetails(studentId: Int): StudentProfileDetailsResponse
    suspend fun getAttendanceByYear(studentId: Int, yearId: Int): com.app.ecarepro.core.domain.model.AttendanceYearResponse
    suspend fun getMonthlyAttendanceDetail(studentId: Int, from: String, till: String, yearId: Int): com.app.ecarepro.core.domain.model.MonthlyAttendanceDetailResponse
}
