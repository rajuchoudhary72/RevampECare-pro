package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun login(
        userName: String,
        password: String,
        schoolCode: String,
    ): Flow<Result<User>>
}