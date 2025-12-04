package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.TeacherTimetable
import kotlinx.coroutines.flow.Flow
import com.app.ecarepro.core.domain.model.Assignment


interface AcademicRepository {
    fun getTeacherTimeline(teacherId: String? = null): Flow<Result<TeacherTimetable>>
    fun getTeacherAssignments(): Flow<Result<List<Assignment>>>


}