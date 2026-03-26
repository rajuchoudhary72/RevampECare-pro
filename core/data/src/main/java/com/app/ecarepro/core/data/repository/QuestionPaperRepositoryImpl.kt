package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.QuestionPaperResponse
import com.app.ecarepro.core.domain.repository.QuestionPaperRepository
import com.app.ecarepro.core.network.AcademicRemoteDataSource
import com.app.ecarepro.core.network.StaffRemoteDataSource
import com.app.ecarepro.core.network.model.staff.toDomainModel
import javax.inject.Inject

internal class QuestionPaperRepositoryImpl @Inject constructor(
    private val academicRemoteDataSource: AcademicRemoteDataSource,
    private val staffRemoteDataSource: StaffRemoteDataSource,
) : QuestionPaperRepository {

    override suspend fun getStaffClasses(): List<Class> {
        return staffRemoteDataSource.getMyClasses().map { it.toDomainModel() }
    }

    override suspend fun getQuestionPapers(classId: Int, yrId: Int): QuestionPaperResponse {
        return academicRemoteDataSource.getQuestionPapers(classId, yrId)
    }
}
