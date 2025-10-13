package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.OnboardingItem
import com.app.ecarepro.core.domain.model.School
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    suspend fun getOnboardingData(): List<OnboardingItem>

    suspend fun getSchoolDetails(schoolCode:String): Flow<Result<String>>

    suspend fun getSchools(): Flow<Result<List<School>>>
}