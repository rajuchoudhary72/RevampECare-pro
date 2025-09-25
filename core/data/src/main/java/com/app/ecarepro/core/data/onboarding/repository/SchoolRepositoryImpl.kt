package com.app.ecarepro.core.data.onboarding.repository

import com.app.ecarepro.core.data.onboarding.mapper.toDomainModel
import com.app.ecarepro.core.domain.model.OnboardingItem
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.network.SchoolRemoteDataSource
import javax.inject.Inject

class SchoolRepositoryImpl @Inject constructor(
    private val schoolRemoteDataSource: SchoolRemoteDataSource
) : SchoolRepository {
    override suspend fun getOnboardingData(): List<OnboardingItem> {
        return schoolRemoteDataSource.getOnboardingData().map { it.toDomainModel() }
    }
}