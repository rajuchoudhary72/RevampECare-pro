package com.app.ecarepro.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.app.ecarepro.data.database.model.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)
}