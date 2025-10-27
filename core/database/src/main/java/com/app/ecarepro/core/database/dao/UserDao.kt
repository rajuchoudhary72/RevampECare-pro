package com.app.ecarepro.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.ecarepro.core.database.model.UserEntity
import com.app.ecarepro.core.domain.model.HomeScreenType

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE user_id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE is_active = 1 LIMIT 1")
    suspend fun getActiveUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)


    @Query("UPDATE users SET home_screen_type = :homeScreenTypeId WHERE is_active = 1")
    suspend fun updateActiveUserHomeScreenType(homeScreenTypeId: Int)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET is_active = 0")
    suspend fun clearActiveUser()

    @Query("DELETE FROM users WHERE user_id = :id")
    suspend fun deleteUserById(id: String)

    @Query("DELETE FROM users")
    suspend fun deleteAll()

    @Query("SELECT auth_token FROM users WHERE is_active = 1 LIMIT 1")
    suspend fun getActiveUserToken(): String?

    @Transaction
    suspend fun setActiveUser(user: UserEntity) {
        clearActiveUser()
        insertOrUpdateUser(user)
    }

}