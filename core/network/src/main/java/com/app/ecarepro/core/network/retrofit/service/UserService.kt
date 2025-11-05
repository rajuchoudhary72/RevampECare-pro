package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.user.NetworkGetCredentialRequest
import com.app.ecarepro.core.network.model.user.NetworkGetCredentialsResponse
import com.app.ecarepro.core.network.model.user.NetworkGetUsernameByUIDResponse
import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import com.app.ecarepro.core.network.model.user.NetworkResendOtpRequest
import com.app.ecarepro.core.network.model.user.NetworkValidateOtpRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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

    @POST("User/GetCredentials")
    suspend fun getCredentials(
        @Body request: NetworkGetCredentialRequest,
    ): NetworkGetCredentialsResponse

    @GET("User/GetUsernameByUID")
    suspend fun getUsernameByUID(
        @Query("SchCode") schoolCode: String,
        @Query("UserID") userID: Int,
        @Query("UserType") userType: Int,
        @Query("RcvOn") receivedOn: String,
    ): NetworkGetUsernameByUIDResponse

}