package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.Assignment
import com.app.ecarepro.core.domain.model.TeacherTimetable
import com.app.ecarepro.core.domain.repository.AcademicRepository
import com.app.ecarepro.core.network.AcademicRemoteDataSource
import com.app.ecarepro.core.network.model.academic.toDomainModel
import com.app.ecarepro.core.network.model.academic.toTeacherTimetable
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AcademicRepositoryImpl @Inject constructor(
    private val academicRemoteDataSource: AcademicRemoteDataSource,
) : AcademicRepository {
    override fun getTeacherTimeline(teacherId: String?): Flow<Result<TeacherTimetable>> {
        return asResultFlow {
            academicRemoteDataSource.getTeacherTimeline(teacherId).toTeacherTimetable()
        }
    }

    override fun getTeacherAssignments(): Flow<Result<List<Assignment>>> {
        return asResultFlow {
            academicRemoteDataSource.getTeacherAssignments().map { it.toDomainModel() }
        }
    }
}