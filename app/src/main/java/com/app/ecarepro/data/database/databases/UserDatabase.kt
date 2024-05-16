package com.app.ecarepro.data.database.databases

import com.app.ecarepro.data.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserDatabase {
    suspend fun insertUser(user: UserEntity)
    suspend fun getUser(userId: Int): UserEntity
    fun getUserFlow(userId: Int): Flow<UserEntity>
    fun getUsersFlow(): Flow<List<UserEntity>>
    suspend fun deleteUser(userEntity: UserEntity)
}