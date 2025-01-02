package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName
import com.app.ecarepro.data.network.CreateUserSessionRequestDto


data class UserLoginRequestDto(
    @SerializedName("password")
    val password: String?,
    @SerializedName("schCode")
    val schCode: String?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("deviceinfo")
    val deviceInfo: CreateUserSessionRequestDto? = null
)