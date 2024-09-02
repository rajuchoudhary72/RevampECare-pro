package com.app.ecarepro.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.app.ecarepro.data.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)
    @Query("DELETE FROM users WHERE user_id = :userId")
    fun deleteUser(userId: Int)
    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    suspend fun getUser(userId: Int): UserEntity

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    fun getUserFlow(userId: Int): Flow<UserEntity>

    @Query("SELECT * FROM users")
    fun getUsersFlow(): Flow<List<UserEntity>>

    @Delete
    fun deleteUser(userEntity: UserEntity)

    @Query("DELETE FROM users")
    suspend fun nukeTable()
}