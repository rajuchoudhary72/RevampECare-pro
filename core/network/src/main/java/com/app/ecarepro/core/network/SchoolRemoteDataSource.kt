package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.NetworkOnboardingItem

import com.app.ecarepro.core.network.model.NetworkSchool
import com.app.ecarepro.core.network.model.NetworkSchoolDetails

interface SchoolRemoteDataSource {
    suspend fun getOnboardingData(): List<NetworkOnboardingItem>
    suspend fun getSchoolDetails(schoolCode: String): NetworkSchoolDetails
    suspend fun getSchools(): List<NetworkSchool>
}