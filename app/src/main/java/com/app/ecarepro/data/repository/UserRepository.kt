package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.NetworkUser
import com.app.ecarepro.data.network.model.VerifyUserDto

interface UserRepository {
    suspend fun insertUser(user: NetworkUser)

    suspend fun verifyUser(schoolCode: String, username: String): VerifyUserDto
    suspend fun getCredentials(
        schoolCode: String,
        userType: Int,
        rcvOn: String,
        mobile: String?,
        email: String?
    ): VerifyUserDto
}