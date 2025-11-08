package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.UserRemoteDataSource
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.model.user.NetworkGetCredentialRequest
import com.app.ecarepro.core.network.model.user.NetworkGetCredentialsResponse
import com.app.ecarepro.core.network.model.user.NetworkGetUsernameByUIDResponse
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

    override suspend fun getCredentials(request: NetworkGetCredentialRequest): NetworkGetCredentialsResponse {
        return userService
            .getCredentials(request)
            .unwrapPayload(successCode = intArrayOf(0,2)) { this }
    }

    override suspend fun getUsernameByUID(
        schoolCode: String,
        userID: Int,
        userType: Int,
        receivedOn: String,
    ): NetworkGetUsernameByUIDResponse {
        return userService
            .getUsernameByUID(
                schoolCode = schoolCode,
                userID = userID,
                userType = userType,
                receivedOn = receivedOn
            )
            .unwrapPayload(successCode = intArrayOf(1,0)) { this }
    }

}