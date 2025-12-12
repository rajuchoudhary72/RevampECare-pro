package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.academic.NetworkAssignment
import com.app.ecarepro.core.network.model.academic.NetworkTeacherTimetable
import com.app.ecarepro.core.network.model.academic.NetworkAssignmentSubmissionReport
import com.app.ecarepro.core.network.model.academic.NetworkSaveAssignment


interface AcademicRemoteDataSource {
    suspend fun getTeacherTimeline(
        teacherId: String? = null,
    ): NetworkTeacherTimetable
    suspend fun getTeacherAssignments(): List<NetworkAssignment>
    suspend fun getAssignmentSubmissionReport(
        id: String,
        submitted: Boolean,
    ): NetworkAssignmentSubmissionReport
    suspend fun saveAssignment(saveAssignment: NetworkSaveAssignment): CommonNetworkResponse
}