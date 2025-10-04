package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.OnboardingSlide

interface SchoolRemoteDataSource {
    suspend fun getOnboardingData(): List<OnboardingSlide>
}