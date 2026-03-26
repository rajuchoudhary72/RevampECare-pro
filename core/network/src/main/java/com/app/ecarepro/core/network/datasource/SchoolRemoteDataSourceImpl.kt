package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.domain.exception.ApiException
import com.app.ecarepro.core.network.SchoolRemoteDataSource
import com.app.ecarepro.core.network.model.feed.NetworkFeedResponse
import com.app.ecarepro.core.network.model.onboarding.NetworkOnboardingItem
import com.app.ecarepro.core.network.model.school.NetworkSchool
import com.app.ecarepro.core.network.model.school.NetworkSchoolDetails
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

    override suspend fun getSchoolDetails(schoolCode: String): NetworkSchoolDetails {
        return schoolService
            .getSchoolDetails(schoolCode)
            .unwrapPayload { this }
    }

    override suspend fun getSchools(): List<NetworkSchool> {
        return schoolService
            .getSchools()
            .unwrapPayload { list ?: emptyList() }
    }

    override suspend fun getFeed(isDashboard: Boolean, page: Int): NetworkFeedResponse {
        val response = schoolService.getFeed(isDashboard, page)
        if (response.errorCode != 0) throw ApiException(response.errorCode, response.message)
        return response
    }
}