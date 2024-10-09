package com.app.ecarepro.data.database.databases

import com.app.ecarepro.data.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserDatabase {
    suspend fun insertUser(user: UserEntity):Long
    suspend fun getUser(userId: Int): UserEntity?
    suspend fun getUser(userId: Int, schoolCode: String, userType:Int): UserEntity?
    fun getUserFlow(userId: Int): Flow<UserEntity?>
    fun getUsersFlow(): Flow<List<UserEntity>?>
    suspend fun deleteUser(userEntity: UserEntity)
    suspend fun deleteUser(userId: Int)
    suspend fun deleteUserById(id: Int)

}