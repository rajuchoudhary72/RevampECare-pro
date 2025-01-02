package com.app.ecarepro.data.network

import android.os.Build
import com.app.ecarepro.BuildConfig
import com.google.gson.annotations.SerializedName

data class CreateUserSessionRequestDto(
    @SerializedName("appVersion")
    val appVersion: String = BuildConfig.VERSION_NAME,
    @SerializedName("deviceType")
    val deviceType: Int = 1,
    @SerializedName("ipAddress")
    val ipAddress: String,
    @SerializedName("locationCity")
    val locationCity: String,
    @SerializedName("model")
    val model: String = Build.MODEL,
    @SerializedName("osVersion")
    val osVersion: String = Build.VERSION.RELEASE,
    @SerializedName("oldSessionID")
    val oldSessionID: String? = null
)


data class UserSessionResponseDto(
    @SerializedName("errorCode")
    val errorCode: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("sessionID")
    val sessionID: String
)

