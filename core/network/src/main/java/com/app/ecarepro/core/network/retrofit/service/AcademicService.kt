package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.academic.NetworkTeacherAssignment
import com.app.ecarepro.core.network.model.academic.NetworkTeacherTimetable
import retrofit2.http.GET
import retrofit2.http.Query

interface AcademicService {
    @GET("Academic/TeachersTimetable")
    suspend fun getSchoolOnboarding(
        @Query("ID") teacherId: String? = null,
    ): NetworkTeacherTimetable
    @GET("Academic/TeachersAssignment")
    suspend fun getTeacherAssignments(): NetworkTeacherAssignment
}