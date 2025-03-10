package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class UserLoginRequestDto(
    @SerializedName("password")
    val password: String?,
    @SerializedName("schCode")
    val schCode: String?,
    @SerializedName("username")
    val username: String?
)