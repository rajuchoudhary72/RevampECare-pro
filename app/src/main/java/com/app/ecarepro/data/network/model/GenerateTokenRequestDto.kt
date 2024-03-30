package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class GenerateTokenRequestDto(
    @SerializedName("ErrorCode")
    val errorCode: Int? = 0,
    @SerializedName("Password")
    val password: String? = "07Pro2019",
    @SerializedName("Username")
    val username: String? = "FSPL"
)