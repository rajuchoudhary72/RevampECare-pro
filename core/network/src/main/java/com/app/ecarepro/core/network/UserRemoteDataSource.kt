package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.user.NetworkGetCredentialRequest
import com.app.ecarepro.core.network.model.user.NetworkGetCredentialsResponse
import com.app.ecarepro.core.network.model.user.NetworkGetUsernameByUIDResponse
import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import com.app.ecarepro.core.network.model.user.NetworkResendOtpRequest
import com.app.ecarepro.core.network.model.user.NetworkValidateOtpRequest

interface UserRemoteDataSource {
    suspend fun login(request: NetworkLoginRequest): NetworkLoginResponse

    suspend fun resendOtp(request: NetworkResendOtpRequest): NetworkLoginResponse

    suspend fun validateOtp(request: NetworkValidateOtpRequest): NetworkLoginResponse

    suspend fun getCredentials(request: NetworkGetCredentialRequest): NetworkGetCredentialsResponse

    suspend fun getUsernameByUID(
        schoolCode: String,
        userID: Int,
        userType: Int,
        receivedOn: String,
    ): NetworkGetUsernameByUIDResponse
}