package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class LoginResponseDto(
    @SerializedName("authToken")
    val authToken: String?,
    @SerializedName("authenticated")
    val authenticated: Boolean?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("userType")
    val userType: Int,
    @SerializedName("roleName")
    val roleName: String,

)