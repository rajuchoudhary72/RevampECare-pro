package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.Assignment
import com.app.ecarepro.core.domain.model.AssignmentSubmissionReport
import com.app.ecarepro.core.domain.model.SaveAssignment
import com.app.ecarepro.core.domain.model.TeacherTimetable
import kotlinx.coroutines.flow.Flow

interface AcademicRepository {
    fun getTeacherTimeline(teacherId: String? = null): Flow<Result<TeacherTimetable>>
    fun getTeacherAssignments(): Flow<Result<List<Assignment>>>
    fun getAssignmentSubmissionReport(
        id: String,
        submitted: Boolean,
    ): Flow<Result<AssignmentSubmissionReport>>

    fun saveAssignment(
        assignment: SaveAssignment,
    ): Flow<Result<String>>

    fun deleteAssignment(id: String): Flow<Result<String>>

}