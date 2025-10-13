package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.onboarding.NetworkOnboardingItem
import com.app.ecarepro.core.network.model.school.NetworkSchool
import com.app.ecarepro.core.network.model.school.NetworkSchoolDetails

interface SchoolRemoteDataSource {
    suspend fun getOnboardingData(): List<NetworkOnboardingItem>
    suspend fun getSchoolDetails(schoolCode: String): NetworkSchoolDetails
    suspend fun getSchools(): List<NetworkSchool>
}