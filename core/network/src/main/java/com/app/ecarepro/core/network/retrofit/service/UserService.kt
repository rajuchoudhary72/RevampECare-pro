package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import com.app.ecarepro.core.network.model.user.NetworkResendOtpRequest
import com.app.ecarepro.core.network.model.user.NetworkValidateOtpRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("User/TwoFactorLogin")
    suspend fun login(
        @Body loginRequest: NetworkLoginRequest,
    ): NetworkLoginResponse

    @POST("User/ResendOTP")
    suspend fun resendOtp(
        @Body loginRequest: NetworkResendOtpRequest,
    ): NetworkLoginResponse

    @POST("User/ValidateOTP")
    suspend fun validateOtp(
        @Body loginRequest: NetworkValidateOtpRequest,
    ): NetworkLoginResponse

}