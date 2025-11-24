package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.Syllabus
import com.app.ecarepro.core.domain.repository.AdminRepository
import com.app.ecarepro.core.network.AdminRemoteDataSource
import com.app.ecarepro.core.network.model.admin.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.collections.map

internal class AdminRepositoryImpl @Inject constructor(
    private val adminRemoteDataSource: AdminRemoteDataSource,
) : AdminRepository {


    override fun getSyllabus(): Flow<Result<List<Syllabus>>> {
        return asResultFlow {
            adminRemoteDataSource.getSyllabus().map { it.toDomainModel() }
        }
    }

    override fun deleteSyllabus(syllabusId: String): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }
}