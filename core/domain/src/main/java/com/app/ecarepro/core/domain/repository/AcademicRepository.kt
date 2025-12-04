package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.Assignment
import com.app.ecarepro.core.domain.model.TeacherTimetable
import kotlinx.coroutines.flow.Flow

interface AcademicRepository {
    fun getTeacherTimeline(teacherId: String? = null): Flow<Result<TeacherTimetable>>
    fun getTeacherAssignments(): Flow<Result<List<Assignment>>>
}