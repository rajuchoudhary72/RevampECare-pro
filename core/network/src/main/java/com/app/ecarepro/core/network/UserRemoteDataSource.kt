package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import com.app.ecarepro.core.network.model.user.NetworkResendOtpRequest
import com.app.ecarepro.core.network.model.user.NetworkValidateOtpRequest

interface UserRemoteDataSource {
    suspend fun login(request: NetworkLoginRequest): NetworkLoginResponse

    suspend fun resendOtp(request: NetworkResendOtpRequest): NetworkLoginResponse

    suspend fun validateOtp(request: NetworkValidateOtpRequest): NetworkLoginResponse
}