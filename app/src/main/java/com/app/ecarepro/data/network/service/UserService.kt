package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.GetCredentialsRequest
import com.app.ecarepro.data.network.model.VerifyUserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {
    @GET("User/Verify")
    suspend fun verifyUser(
        @Query("SchCode") schoolCode: String,
        @Query("Username") username: String
    ): VerifyUserDto

    @POST("User/GetCredentials")
    suspend fun getCredentials(
        @Body request: GetCredentialsRequest,
    ): VerifyUserDto

}