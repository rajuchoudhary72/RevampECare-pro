package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.NetworkResponse
import com.app.ecarepro.core.network.model.academic.NetworkTeacherTimetable
import com.app.ecarepro.core.network.model.onboarding.NetworkSchoolOnboarding
import retrofit2.http.GET
import retrofit2.http.Query

interface AcademicService {
    @GET("Academic/TeachersTimetable")
    suspend fun getSchoolOnboarding(
        @Query("ID") teacherId: String? = null,
    ): NetworkTeacherTimetable
}