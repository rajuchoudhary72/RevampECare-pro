package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.TeacherTimetable
import kotlinx.coroutines.flow.Flow
import com.app.ecarepro.core.domain.model.Assignment
import com.app.ecarepro.core.domain.model.AssignmentSubmissionReport
import com.app.ecarepro.core.domain.model.SaveAssignment


interface AcademicRepository {
    fun getTeacherTimeline(teacherId: String? = null): Flow<Result<TeacherTimetable>>
    fun getTeacherAssignments(): Flow<Result<List<Assignment>>>
    fun getAssignmentSubmissionReport(
        id: String,
        submitted: Boolean
    ): Flow<Result<AssignmentSubmissionReport>>
    fun saveAssignment(
        assignment: SaveAssignment,
    ): Flow<Result<String>>
}