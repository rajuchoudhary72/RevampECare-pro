package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.AcademicRemoteDataSource
import com.app.ecarepro.core.network.model.NetworkResponse
import com.app.ecarepro.core.network.model.academic.NetworkTeacherTimetable
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.AcademicService
import javax.inject.Inject
import com.app.ecarepro.core.network.model.academic.NetworkAssignment
import com.app.ecarepro.core.network.model.academic.NetworkTeacherAssignment
import com.app.ecarepro.core.domain.model.Assignment


internal class AcademicRemoteDataSourceImpl @Inject constructor(
    private val academicService: AcademicService,
) : AcademicRemoteDataSource {
    override suspend fun getTeacherTimeline(teacherId: String?): NetworkTeacherTimetable {
        return academicService
            .getSchoolOnboarding(teacherId)
            .unwrapPayload { this }
    }
    override suspend fun getTeacherAssignments(): List<NetworkAssignment> {
        return academicService
            .getTeacherAssignments()
            .unwrapPayload { assignments?:emptyList() }
    }
}