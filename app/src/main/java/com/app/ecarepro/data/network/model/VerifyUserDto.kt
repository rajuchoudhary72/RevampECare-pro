package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class VerifyUserDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("isVerified")
    val isVerified: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("userType")
    val userType: Int?
)