package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.NetworkResponse
import com.app.ecarepro.core.network.model.academic.NetworkTeacherTimetable
import com.app.ecarepro.core.network.model.onboarding.NetworkOnboardingItem
import com.app.ecarepro.core.network.model.school.NetworkSchool
import com.app.ecarepro.core.network.model.school.NetworkSchoolDetails

interface AcademicRemoteDataSource {
    suspend fun getTeacherTimeline(
        teacherId: String? = null,
    ): NetworkTeacherTimetable
}