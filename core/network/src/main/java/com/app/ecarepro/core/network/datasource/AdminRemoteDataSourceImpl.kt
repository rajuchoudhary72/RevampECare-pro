package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.AdminRemoteDataSource
import com.app.ecarepro.core.network.model.admin.NetworkAssignHouseItem
import com.app.ecarepro.core.network.model.admin.NetworkUploadStudentPhotoRequest
import com.app.ecarepro.core.network.model.admin.NetworkAssignRollNumberItem
import com.app.ecarepro.core.network.model.admin.NetworkGetClassPromotion
import com.app.ecarepro.core.network.model.admin.NetworkGetHouseStudents
import com.app.ecarepro.core.network.model.admin.NetworkRollNumberStudent
import com.app.ecarepro.core.network.model.admin.NetworkSaveClassPromotionRequest
import com.app.ecarepro.core.network.model.admin.NetworkSaveSyllabus
import com.app.ecarepro.core.network.model.admin.NetworkStaffProfile
import com.app.ecarepro.core.network.model.admin.NetworkStudentProfile
import com.app.ecarepro.core.network.model.admin.NetworkSyllabus
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.AdminService
import javax.inject.Inject

internal class AdminRemoteDataSourceImpl @Inject constructor(
    private val adminService: AdminService,
) : AdminRemoteDataSource {

    override suspend fun getClassPromotion(classId: String): NetworkGetClassPromotion {
        return adminService.getClassPromotion(classId).unwrapPayload { this }
    }

    override suspend fun saveClassPromotion(request: NetworkSaveClassPromotionRequest): String {
        return adminService.saveClassPromotion(request).unwrapPayload { message }
    }

    override suspend fun getStudentsForRollNumber(classId: String, orderBy: Int): List<NetworkRollNumberStudent> {
        return adminService.getStudentsForRollNumber(classId, orderBy).unwrapPayload { students ?: emptyList() }
    }

    override suspend fun assignRollNumbers(assignments: List<NetworkAssignRollNumberItem>): String {
        return adminService.assignRollNumbers(assignments).unwrapPayload { message }
    }

    override suspend fun getStudentsForHouse(classId: String, orderBy: Int): NetworkGetHouseStudents {
        return adminService.getStudentsForHouse(classId, orderBy).unwrapPayload { this }
    }

    override suspend fun assignHouses(assignments: List<NetworkAssignHouseItem>): String {
        return adminService.assignHouses(assignments).unwrapPayload { message }
    }

    override suspend fun uploadStudentPhoto(request: NetworkUploadStudentPhotoRequest): String {
        return adminService.uploadStudentPhoto(request).unwrapPayload { message }
    }

    override suspend fun getSyllabus(): List<NetworkSyllabus> {
        return adminService.getSyllabus().unwrapPayload { syllabuses }
    }

    override suspend fun deleteSyllabus(syllabusId: String): Boolean {
        return adminService.deleteSyllabus(syllabusId).unwrapPayload { true }
    }

    override suspend fun saveSyllabus(syllabus: NetworkSaveSyllabus): String {
        return adminService.saveSyllabus(syllabus).unwrapPayload { message }
    }

    override suspend fun getStudentProfiles(scholarType: Int?): List<NetworkStudentProfile> {
        return adminService.getStudentProfiles(scholarType).unwrapPayload { students }
    }

    override suspend fun getStaffProfiles(): List<NetworkStaffProfile> {
        return adminService.getStaffProfiles().unwrapPayload { staff }
    }

    override suspend fun getStudentProfileDetails(studentId: Int): com.app.ecarepro.core.domain.model.StudentProfileDetailsResponse {
        return adminService.getStudentProfileDetails(studentId)
    }

    override suspend fun getStaffProfileDetails(staffId: Int): com.app.ecarepro.core.domain.model.StaffProfileDetailsResponse {
        return adminService.getStaffProfileDetails(staffId)
    }

    override suspend fun getAttendanceByYear(
        studentId: Int,
        yearId: Int
    ): com.app.ecarepro.core.domain.model.AttendanceYearResponse {
        return adminService.getAttendanceByYear(studentId, yearId)
    }

    override suspend fun getMonthlyAttendanceDetail(
        studentId: Int,
        from: String,
        till: String,
        yearId: Int
    ): com.app.ecarepro.core.domain.model.MonthlyAttendanceDetailResponse {
        return adminService.getMonthlyAttendanceDetail(studentId, from, till, yearId)
    }

}