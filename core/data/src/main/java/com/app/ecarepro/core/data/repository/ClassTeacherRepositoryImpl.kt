package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.ClassTeacher
import com.app.ecarepro.core.domain.repository.ClassTeacherRepository
import com.app.ecarepro.core.network.ClassTeacherRemoteDataSource
import com.app.ecarepro.core.network.model.classteacher.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ClassTeacherRepositoryImpl @Inject constructor(
    private val classTeacherRemoteDataSource: ClassTeacherRemoteDataSource,
) : ClassTeacherRepository {

    override fun getClassTeachers(): Flow<Result<List<ClassTeacher>>> = asResultFlow {
        classTeacherRemoteDataSource.getClassTeachers().map { it.toDomainModel() }
    }
}
