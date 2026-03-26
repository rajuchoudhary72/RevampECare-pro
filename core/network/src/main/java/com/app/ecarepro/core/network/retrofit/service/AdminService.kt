package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.domain.model.StaffProfileDetailsResponse
import com.app.ecarepro.core.domain.model.StudentProfileDetailsResponse
import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.academic.NetworkTeacherTimetable
import com.app.ecarepro.core.network.model.admin.NetworkDeleteSyllabus
import com.app.ecarepro.core.network.model.admin.NetworkGetClassPromotion
import com.app.ecarepro.core.network.model.admin.NetworkGetStaffProfiles
import com.app.ecarepro.core.network.model.admin.NetworkGetStudentProfiles
import com.app.ecarepro.core.network.model.admin.NetworkGetSyllabuses
import com.app.ecarepro.core.network.model.admin.NetworkAssignHouseItem
import com.app.ecarepro.core.network.model.admin.NetworkUploadStudentPhotoRequest
import com.app.ecarepro.core.network.model.admin.NetworkAssignRollNumberItem
import com.app.ecarepro.core.network.model.admin.NetworkGetHouseStudents
import com.app.ecarepro.core.network.model.admin.NetworkGetRollNumberStudents
import com.app.ecarepro.core.network.model.admin.NetworkSaveClassPromotionRequest
import com.app.ecarepro.core.network.model.admin.NetworkSaveSyllabus
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AdminService {

    @GET("Admin/ClassPromotion")
    suspend fun getClassPromotion(
        @Query("ClassID") classId: String,
    ): NetworkGetClassPromotion

    @POST("Admin/SaveClassPromotion")
    suspend fun saveClassPromotion(
        @Body request: NetworkSaveClassPromotionRequest,
    ): CommonNetworkResponse



    @GET("Admin/StudentListToAssignRollNo")
    suspend fun getStudentsForRollNumber(
        @Query("ID") classId: String,
        @Query("Orderby") orderBy: Int = 0,
    ): NetworkGetRollNumberStudents

    @POST("Admin/AssignRollNumber")
    suspend fun assignRollNumbers(
        @Body assignments: List<NetworkAssignRollNumberItem>,
    ): CommonNetworkResponse

    @GET("Admin/StudentListToAssignHouse")
    suspend fun getStudentsForHouse(
        @Query("ID") classId: String,
        @Query("Orderby") orderBy: Int = 0,
    ): NetworkGetHouseStudents

    @POST("Admin/AssignHouse")
    suspend fun assignHouses(
        @Body assignments: List<NetworkAssignHouseItem>,
    ): CommonNetworkResponse

    @POST("Admin/UploadStudentPhoto")
    suspend fun uploadStudentPhoto(
        @Body request: NetworkUploadStudentPhotoRequest,
    ): CommonNetworkResponse

    @GET("Admin/Syllabuses")
    suspend fun getSyllabus(): NetworkGetSyllabuses

    @GET("Admin/DeleteSyllabus")
    suspend fun deleteSyllabus(
        @Query("ID") teacherId: String? = null,
    ): NetworkDeleteSyllabus

    @POST("Admin/SaveSyllabus")
    suspend fun saveSyllabus(
        @Body request: NetworkSaveSyllabus,
    ): CommonNetworkResponse

    @GET("Report/StudentList")
    suspend fun getStudentProfiles(
        @Query("ScholarType") scholarType: Int? = null
    ): NetworkGetStudentProfiles

    @GET("Report/StaffList")
    suspend fun getStaffProfiles(): NetworkGetStaffProfiles

    @GET("Student/Profile")
    suspend fun getStudentProfileDetails(
        @Query("StID") studentId: Int
    ): StudentProfileDetailsResponse

    @GET("Staff/Profile")
    suspend fun getStaffProfileDetails(
        @Query("SID") staffId: Int
    ): StaffProfileDetailsResponse

    @GET("Report/AttendanceYrID")
    suspend fun getAttendanceByYear(
        @Query("StID") studentId: Int,
        @Query("YrID") yearId: Int
    ): com.app.ecarepro.core.domain.model.AttendanceYearResponse

    @GET("Student/Attendance")
    suspend fun getMonthlyAttendanceDetail(
        @Query("ID") studentId: Int,
        @Query("From") from: String,
        @Query("Till") till: String,
        @Query("YrID") yearId: Int
    ): com.app.ecarepro.core.domain.model.MonthlyAttendanceDetailResponse

}