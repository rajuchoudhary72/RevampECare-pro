package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.StaffProfile
import com.app.ecarepro.core.domain.model.StaffProfileDetailsResponse
import com.app.ecarepro.core.domain.repository.StaffProfileRepository
import com.app.ecarepro.core.network.AdminRemoteDataSource
import com.app.ecarepro.core.network.model.admin.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class StaffProfileRepositoryImpl @Inject constructor(
    private val adminRemoteDataSource: AdminRemoteDataSource,
) : StaffProfileRepository {

    override fun getStaffProfiles(): Flow<Result<List<StaffProfile>>> {
        return asResultFlow {
            adminRemoteDataSource.getStaffProfiles().map { it.toDomainModel() }
        }
    }

    override suspend fun getStaffProfileDetails(staffId: Int): StaffProfileDetailsResponse {
        return adminRemoteDataSource.getStaffProfileDetails(staffId)
    }
}
