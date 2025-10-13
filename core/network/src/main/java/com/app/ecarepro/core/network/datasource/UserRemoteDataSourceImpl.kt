package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.UserRemoteDataSource
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import com.app.ecarepro.core.network.retrofit.service.UserService
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val userService: UserService,
) : UserRemoteDataSource {

    override suspend fun login(loginRequest: NetworkLoginRequest): NetworkLoginResponse {
        return userService
            .login(loginRequest)
            .unwrapPayload { this }
    }

}