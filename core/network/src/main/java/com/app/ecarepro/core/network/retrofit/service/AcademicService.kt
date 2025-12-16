package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.academic.NetworkTeacherAssignment
import com.app.ecarepro.core.network.model.academic.NetworkTeacherTimetable
import retrofit2.http.GET
import retrofit2.http.Query
import com.app.ecarepro.core.network.model.academic.NetworkAssignmentSubmissionReport
import com.app.ecarepro.core.network.model.academic.NetworkSaveAssignment
import retrofit2.http.Body
import retrofit2.http.POST


interface AcademicService {
    @GET("Academic/TeachersTimetable")
    suspend fun getSchoolOnboarding(
        @Query("ID") teacherId: String? = null,
    ): NetworkTeacherTimetable

    @GET("Academic/TeachersAssignment")
    suspend fun getTeacherAssignments(): NetworkTeacherAssignment

    @GET("Academic/AssignmnetSubmissionRPT")
    suspend fun getAssignmentSubmissionReport(
        @Query("ID") id: String,
        @Query("NotSubmitted") notSubmitted: Boolean,
    ): NetworkAssignmentSubmissionReport

    @GET("Academic/DeleteAssignment")
    suspend fun deleteAssignment(
        @Query("ID") id: String
    ): CommonNetworkResponse
    @POST("Academic/PostAssignment")
    suspend fun saveAssignment(
        @Body request: NetworkSaveAssignment,
    ): CommonNetworkResponse
}