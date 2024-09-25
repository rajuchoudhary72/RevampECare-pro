package com.app.ecarepro.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.ecarepro.data.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity):Long

    @Query("DELETE FROM users WHERE user_id = :userId")
    fun deleteUser(userId: Int)

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    suspend fun getUser(userId: Int): UserEntity

    @Query("SELECT * FROM users WHERE user_id = :userId AND schoolCode = :schoolCode AND userType = :userType LIMIT 1")
    suspend fun getUser(userId: Int, schoolCode:String, userType:Int): UserEntity

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    fun getUserFlow(userId: Int): Flow<UserEntity>

    @Query("SELECT * FROM users")
    fun getUsersFlow(): Flow<List<UserEntity>>

    @Delete
    fun deleteUser(userEntity: UserEntity)

    @Query("DELETE FROM users")
    suspend fun nukeTable()
}