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
import com.app.ecarepro.core.network.model.academic.NetworkDeleteAssignment
import com.app.ecarepro.core.network.model.academic.toDomainModel
import kotlinx.serialization.InternalSerializationApi

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
    override suspend fun deleteAssignment(deleteAssignment: NetworkDeleteAssignment): CommonNetworkResponse {
        return academicService.deleteAssignment(
            deleteAssignment.id
        ).unwrapPayload { this }
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun getQuestionPapers(
        classId: Int,
        yrId: Int,
    ): com.app.ecarepro.core.domain.model.QuestionPaperResponse {
        return academicService.getQuestionPapers(classId, yrId).toDomainModel()
    }
}