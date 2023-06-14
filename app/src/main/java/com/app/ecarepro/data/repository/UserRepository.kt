package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.NetworkUser

interface UserRepository {
    suspend fun insertUser(user:NetworkUser)
}