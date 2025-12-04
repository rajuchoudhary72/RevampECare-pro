package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.academic.NetworkAssignment
import com.app.ecarepro.core.network.model.academic.NetworkTeacherTimetable

interface AcademicRemoteDataSource {
    suspend fun getTeacherTimeline(
        teacherId: String? = null,
    ): NetworkTeacherTimetable
    suspend fun getTeacherAssignments(): List<NetworkAssignment>


}