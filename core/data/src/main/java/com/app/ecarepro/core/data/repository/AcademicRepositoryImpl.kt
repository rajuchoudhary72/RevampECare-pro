package com.app.ecarepro.core.data.repository

import android.util.Log
import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.Assignment
import com.app.ecarepro.core.domain.model.AssignmentSubmissionReport
import com.app.ecarepro.core.domain.model.TeacherTimetable
import com.app.ecarepro.core.domain.repository.AcademicRepository
import com.app.ecarepro.core.network.AcademicRemoteDataSource
import com.app.ecarepro.core.network.model.academic.toTeacherTimetable
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.app.ecarepro.core.network.model.academic.toDomainModel
import com.app.ecarepro.core.network.model.academic.toNetworkModel
import com.app.ecarepro.core.domain.model.SaveAssignment
import com.app.ecarepro.core.network.model.academic.NetworkDeleteAssignment


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
    override fun getAssignmentSubmissionReport(
        id: String,
        submitted: Boolean,
    ): Flow<Result<AssignmentSubmissionReport>> {
        return asResultFlow {
            academicRemoteDataSource.getAssignmentSubmissionReport(
                id, submitted
            ).toDomainModel()
        }
    }
    override fun saveAssignment(assignment: SaveAssignment): Flow<Result<String>> {
        Log.e("OKHTTP", assignment.toNetworkModel().toString() )
        return asResultFlow {
            academicRemoteDataSource.saveAssignment(
                assignment.toNetworkModel()
            ).message
        }
    }
    override fun deleteAssignment(id: String): Flow<Result<String>> {
        return asResultFlow {
            academicRemoteDataSource.deleteAssignment(
                NetworkDeleteAssignment(id)
            ).message
        }
    }
}