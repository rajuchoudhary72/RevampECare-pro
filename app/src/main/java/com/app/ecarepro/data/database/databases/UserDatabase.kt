package com.app.ecarepro.data.database.databases

import com.app.ecarepro.data.database.model.UserEntity

interface UserDatabase {
    suspend fun insertUser(user: UserEntity)
}