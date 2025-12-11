package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.academic.NetworkAssignmentSubmissionReport
import com.app.ecarepro.core.network.model.academic.NetworkSaveAssignment
import com.app.ecarepro.core.network.model.academic.NetworkTeacherAssignment
import com.app.ecarepro.core.network.model.academic.NetworkTeacherTimetable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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

    @POST("Academic/PostAssignment")
    suspend fun saveAssignment(
        @Body request: NetworkSaveAssignment,
    ): CommonNetworkResponse
}