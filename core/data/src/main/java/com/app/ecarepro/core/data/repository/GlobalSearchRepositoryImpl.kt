package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.globalsearch.SearchStaff
import com.app.ecarepro.core.domain.model.globalsearch.SearchStudent
import com.app.ecarepro.core.domain.repository.GlobalSearchRepository
import com.app.ecarepro.core.network.GlobalSearchRemoteDataSource
import com.app.ecarepro.core.network.model.globalsearch.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GlobalSearchRepositoryImpl @Inject constructor(
    private val remoteDataSource: GlobalSearchRemoteDataSource,
) : GlobalSearchRepository {

    override fun getStudents(): Flow<Result<List<SearchStudent>>> = asResultFlow {
        remoteDataSource.getStudents().map { it.toDomainModel() }
    }

    override fun getStaff(): Flow<Result<List<SearchStaff>>> = asResultFlow {
        remoteDataSource.getStaff().map { it.toDomainModel() }
    }
}
