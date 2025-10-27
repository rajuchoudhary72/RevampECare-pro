package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.UserRemoteDataSource
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import com.app.ecarepro.core.network.model.user.NetworkResendOtpRequest
import com.app.ecarepro.core.network.model.user.NetworkValidateOtpRequest
import com.app.ecarepro.core.network.retrofit.service.UserService
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val userService: UserService,
) : UserRemoteDataSource {

    override suspend fun login(request: NetworkLoginRequest): NetworkLoginResponse {
        return userService
            .login(request)
            .unwrapPayload { this }
    }

    override suspend fun resendOtp(request: NetworkResendOtpRequest): NetworkLoginResponse {
        return userService
            .resendOtp(request)
            .unwrapPayload { this }
    }

    override suspend fun validateOtp(request: NetworkValidateOtpRequest): NetworkLoginResponse {
        return userService
            .validateOtp(request)
            .unwrapPayload { this }
    }

}