package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.HomeScreenType
import com.app.ecarepro.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun login(
        userName: String,
        password: String,
        schoolCode: String,
        location: String
    ): Flow<Result<User>>

    suspend fun getHomeScreenType(): HomeScreenType

    suspend fun saveHomeScreenType(homeScreenType: HomeScreenType)
    suspend fun getActiveUser(): User?
}