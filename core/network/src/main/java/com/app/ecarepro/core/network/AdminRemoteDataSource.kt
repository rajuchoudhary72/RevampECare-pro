package com.app.ecarepro.core.network

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

interface AdminRemoteDataSource {
    suspend fun getClassPromotion(classId: String): NetworkGetClassPromotion
    suspend fun saveClassPromotion(request: NetworkSaveClassPromotionRequest): String
    suspend fun getStudentsForRollNumber(classId: String, orderBy: Int): List<NetworkRollNumberStudent>
    suspend fun assignRollNumbers(assignments: List<NetworkAssignRollNumberItem>): String
    suspend fun getStudentsForHouse(classId: String, orderBy: Int): NetworkGetHouseStudents
    suspend fun assignHouses(assignments: List<NetworkAssignHouseItem>): String
    suspend fun uploadStudentPhoto(request: NetworkUploadStudentPhotoRequest): String
    suspend fun getSyllabus(): List<NetworkSyllabus>
    suspend fun deleteSyllabus(syllabusId: String): Boolean
    suspend fun saveSyllabus(syllabus: NetworkSaveSyllabus): String
    suspend fun getStudentProfiles(scholarType: Int?): List<NetworkStudentProfile>
    suspend fun getStaffProfiles(): List<NetworkStaffProfile>
    suspend fun getStudentProfileDetails(studentId: Int): com.app.ecarepro.core.domain.model.StudentProfileDetailsResponse
    suspend fun getStaffProfileDetails(staffId: Int): com.app.ecarepro.core.domain.model.StaffProfileDetailsResponse
    suspend fun getAttendanceByYear(studentId: Int, yearId: Int): com.app.ecarepro.core.domain.model.AttendanceYearResponse
    suspend fun getMonthlyAttendanceDetail(studentId: Int, from: String, till: String, yearId: Int): com.app.ecarepro.core.domain.model.MonthlyAttendanceDetailResponse
}