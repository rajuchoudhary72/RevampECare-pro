package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.StaffProfile
import com.app.ecarepro.core.domain.repository.KnowYourTeacherRepository
import com.app.ecarepro.core.network.StaffRemoteDataSource
import com.app.ecarepro.core.network.model.admin.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class KnowYourTeacherRepositoryImpl @Inject constructor(
    private val staffRemoteDataSource: StaffRemoteDataSource,
) : KnowYourTeacherRepository {

    override fun getTeacherList(): Flow<Result<List<StaffProfile>>> = asResultFlow {
        staffRemoteDataSource.getTeacherList().map { it.toDomainModel() }
    }
}
