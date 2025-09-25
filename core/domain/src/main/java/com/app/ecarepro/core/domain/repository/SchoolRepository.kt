package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.OnboardingItem

interface SchoolRepository {
    suspend fun getOnboardingData(): List<OnboardingItem>
}