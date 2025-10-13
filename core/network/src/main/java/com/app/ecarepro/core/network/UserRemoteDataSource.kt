package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse

interface UserRemoteDataSource {
    suspend fun login(loginRequest: NetworkLoginRequest): NetworkLoginResponse
}