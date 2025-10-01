package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.NetworkOnboardingItem

interface SchoolRemoteDataSource {
    suspend fun getOnboardingData(): List<NetworkOnboardingItem>
}