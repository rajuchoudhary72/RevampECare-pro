package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.AcademicRemoteDataSource
import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.academic.NetworkAssignment
import com.app.ecarepro.core.network.model.academic.NetworkAssignmentSubmissionReport
import com.app.ecarepro.core.network.model.academic.NetworkSaveAssignment
import com.app.ecarepro.core.network.model.academic.NetworkTeacherTimetable
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.AcademicService
import javax.inject.Inject

internal class AcademicRemoteDataSourceImpl @Inject constructor(
    private val academicService: AcademicService,
) : AcademicRemoteDataSource {
    override suspend fun getTeacherTimeline(teacherId: String?): NetworkTeacherTimetable {
        return academicService.getSchoolOnboarding(teacherId).unwrapPayload { this }
    }

    override suspend fun getTeacherAssignments(): List<NetworkAssignment> {
        return academicService.getTeacherAssignments().unwrapPayload { assignments ?: emptyList() }
    }

    override suspend fun getAssignmentSubmissionReport(
        id: String,
        submitted: Boolean,
    ): NetworkAssignmentSubmissionReport {
        return academicService.getAssignmentSubmissionReport(
            id = id, notSubmitted = submitted
        ).unwrapPayload { this }
    }

    override suspend fun saveAssignment(saveAssignment: NetworkSaveAssignment): CommonNetworkResponse {
        return academicService.saveAssignment(
            saveAssignment
        ).unwrapPayload { this }
    }
}