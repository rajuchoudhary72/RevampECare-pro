package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkUser
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto

interface UserRepository {
    suspend fun insertUser(user: NetworkUser)

    suspend fun verifyUser(schoolCode: String, username: String): NetworkUserDetailsDto

    suspend fun getCredentials(
        schoolCode: String,
        userType: Int,
        rcvOn: String,
        mobile: String?,
        email: String?
    ): NetworkUserDetailsDto
    suspend fun login(
        schoolCode: String,
        userName: String,
        password: String
    ): LoginResponseDto
}