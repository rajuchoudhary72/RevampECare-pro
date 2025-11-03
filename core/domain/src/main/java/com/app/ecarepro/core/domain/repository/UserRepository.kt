package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.User
import kotlinx.coroutines.flow.Flow
import com.app.ecarepro.core.domain.model.HomeScreenType


interface UserRepository {
    fun login(
        userName: String,
        password: String,
        schoolCode: String,
    ): Flow<Result<User>>
    suspend fun getHomeScreenType(): HomeScreenType

    suspend fun saveHomeScreenType(homeScreenType: HomeScreenType)
    suspend fun getActiveUser(): User?
}