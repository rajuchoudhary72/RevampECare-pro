package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.StudentProfile
import com.app.ecarepro.core.domain.model.StudentProfileDetailsResponse
import com.app.ecarepro.core.domain.repository.StudentProfileRepository
import com.app.ecarepro.core.network.AdminRemoteDataSource
import com.app.ecarepro.core.network.model.admin.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class StudentProfileRepositoryImpl @Inject constructor(
    private val adminRemoteDataSource: AdminRemoteDataSource,
) : StudentProfileRepository {

    override fun getStudentProfiles(scholarType: Int?): Flow<Result<List<StudentProfile>>> {
        return asResultFlow {
            adminRemoteDataSource.getStudentProfiles(scholarType).map { it.toDomainModel() }
        }
    }

    override suspend fun getStudentProfileDetails(studentId: Int): StudentProfileDetailsResponse {
        return adminRemoteDataSource.getStudentProfileDetails(studentId)
    }

    override suspend fun getAttendanceByYear(
        studentId: Int,
        yearId: Int
    ): com.app.ecarepro.core.domain.model.AttendanceYearResponse {
        return adminRemoteDataSource.getAttendanceByYear(studentId, yearId)
    }

    override suspend fun getMonthlyAttendanceDetail(
        studentId: Int,
        from: String,
        till: String,
        yearId: Int
    ): com.app.ecarepro.core.domain.model.MonthlyAttendanceDetailResponse {
        return adminRemoteDataSource.getMonthlyAttendanceDetail(studentId, from, till, yearId)
    }
}
