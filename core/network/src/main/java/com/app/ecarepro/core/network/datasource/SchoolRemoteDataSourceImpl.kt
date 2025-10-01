package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.SchoolRemoteDataSource
import com.app.ecarepro.core.network.model.NetworkOnboardingItem
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.SchoolService
import javax.inject.Inject

class SchoolRemoteDataSourceImpl @Inject constructor(
    private val schoolService: SchoolService
) : SchoolRemoteDataSource {
    override suspend fun getOnboardingData(): List<NetworkOnboardingItem> {
        return schoolService
            .getSchoolOnboarding()
            .unwrapPayload { slides }
    }
}